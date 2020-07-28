package org.simple.clinic.facilitypicker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding3.recyclerview.scrollEvents
import com.jakewharton.rxbinding3.recyclerview.scrollStateChanges
import com.jakewharton.rxbinding3.view.detaches
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.cast
import io.reactivex.rxkotlin.ofType
import kotlinx.android.synthetic.main.view_facilitypicker.view.*
import org.simple.clinic.R
import org.simple.clinic.ReportAnalyticsEvents
import org.simple.clinic.di.injector
import org.simple.clinic.facility.Facility
import org.simple.clinic.facility.change.FacilityListItem
import org.simple.clinic.mobius.MobiusDelegate
import org.simple.clinic.util.unsafeLazy
import org.simple.clinic.widgets.ItemAdapter
import org.simple.clinic.widgets.RecyclerViewUserScrollDetector
import org.simple.clinic.widgets.displayedChildResId
import org.simple.clinic.widgets.hideKeyboard
import javax.inject.Inject

private typealias OnFacilitySelected = (Facility) -> Unit
private typealias OnBackClicked = () -> Unit

class FacilityPickerView(
    context: Context,
    attributeSet: AttributeSet
) : FrameLayout(context, attributeSet), FacilityPickerUi, FacilityPickerUiActions {

  @Inject
  lateinit var effectHandlerFactory: FacilityPickerEffectHandler.Factory

  @Inject
  lateinit var uiRendererFactory: FacilityPickerUiRenderer.Factory

  @Inject
  lateinit var config: FacilityPickerConfig

  var facilitySelectedCallback: OnFacilitySelected? = null

  var backClicked: OnBackClicked? = null

  private val recyclerViewAdapter = ItemAdapter(FacilityListItem.Differ())

  private val pickFrom: PickFrom

  init {
    inflate(context, R.layout.view_facilitypicker, this)

    val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.FacilityPickerView)
    pickFrom = PickFrom.forAttribute(typedArray)
    typedArray.recycle()

    context.injector<Injector>().inject(this)

    toolbarViewWithSearch.setNavigationOnClickListener { backClicked?.invoke() }
    toolbarViewWithoutSearch.setNavigationOnClickListener { backClicked?.invoke() }

    facilityRecyclerView.layoutManager = LinearLayoutManager(context)
    facilityRecyclerView.adapter = recyclerViewAdapter

    searchEditText.requestFocus()

    // Hiding the keyboard without adding a post{} block doesn't seem to work.
    post { hideKeyboard() }
    hideKeyboardOnListScroll()
  }

  private val events: Observable<FacilityPickerEvent> by unsafeLazy {
    Observable
        .merge(
            searchQueryChanges(),
            facilityClicks()
        )
        .compose(ReportAnalyticsEvents())
        .cast<FacilityPickerEvent>()
  }

  private val delegate: MobiusDelegate<FacilityPickerModel, FacilityPickerEvent, FacilityPickerEffect> by unsafeLazy {
    val uiRenderer = uiRendererFactory.create(this)

    MobiusDelegate.forView(
        events = events,
        defaultModel = FacilityPickerModel.create(),
        update = FacilityPickerUpdate(pickFrom),
        effectHandler = effectHandlerFactory.inject(this).build(),
        init = FacilityPickerInit(pickFrom, config),
        modelUpdateListener = uiRenderer::render
    )
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    delegate.start()
  }

  override fun onDetachedFromWindow() {
    delegate.stop()
    super.onDetachedFromWindow()
  }

  override fun onSaveInstanceState(): Parcelable? {
    return delegate.onSaveInstanceState(super.onSaveInstanceState())
  }

  override fun onRestoreInstanceState(state: Parcelable?) {
    super.onRestoreInstanceState(delegate.onRestoreInstanceState(state))
  }

  override fun showProgressIndicator() {
    progressView.visibility = RelativeLayout.VISIBLE
  }

  override fun hideProgressIndicator() {
    progressView.visibility = RelativeLayout.GONE
  }

  override fun showToolbarWithSearchField() {
    toolbarViewFlipper.displayedChildResId = R.id.toolbarViewWithSearch
  }

  override fun showToolbarWithoutSearchField() {
    toolbarViewFlipper.displayedChildResId = R.id.toolbarViewWithoutSearch
  }

  override fun updateFacilities(facilityItems: List<FacilityListItem>) {
    recyclerViewAdapter.submitList(facilityItems)
  }

  override fun dispatchSelectedFacility(facility: Facility) {
    facilitySelectedCallback?.invoke(facility)
  }

  @SuppressLint("CheckResult")
  private fun hideKeyboardOnListScroll() {
    val scrollEvents = facilityRecyclerView.scrollEvents()
    val scrollStateChanges = facilityRecyclerView.scrollStateChanges()

    Observables.combineLatest(scrollEvents, scrollStateChanges)
        .compose(RecyclerViewUserScrollDetector.streamDetections())
        .filter { it.byUser }
        .takeUntil(detaches())
        .subscribe { hideKeyboard() }
  }

  private fun searchQueryChanges(): Observable<FacilityPickerEvent> =
      searchEditText
          .textChanges()
          .map { text -> SearchQueryChanged(text.toString()) }

  private fun facilityClicks(): Observable<FacilityPickerEvent> =
      recyclerViewAdapter
          .itemEvents
          .ofType<FacilityListItem.FacilityItemClicked>()
          .map { FacilityClicked(it.facility) }

  interface Injector {
    fun inject(target: FacilityPickerView)
  }
}
