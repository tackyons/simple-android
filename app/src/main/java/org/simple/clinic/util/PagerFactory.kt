package org.simple.clinic.util

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.rxjava2.observable
import io.reactivex.Observable
import javax.inject.Inject

typealias PagingSourceFactory<K, V> = () -> PagingSource<K, V>

class PagerFactory @Inject constructor() {

  fun <K : Any, V : Any> createPager(
      sourceFactory: PagingSourceFactory<K, V>,
      pageSize: Int,
      enablePlaceholders: Boolean,
      initialKey: K? = null
  ): Observable<PagingData<V>> {
    return Pager(
        config = PagingConfig(
            pageSize = pageSize,
            enablePlaceholders = enablePlaceholders
        ),
        initialKey = initialKey,
        pagingSourceFactory = sourceFactory
    ).observable
  }
}
