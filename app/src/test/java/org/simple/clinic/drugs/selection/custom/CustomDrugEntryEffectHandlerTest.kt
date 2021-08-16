package org.simple.clinic.drugs.selection.custom

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Completable
import org.junit.After
import org.junit.Test
import org.simple.clinic.TestData
import org.simple.clinic.drugs.PrescriptionRepository
import org.simple.clinic.drugs.search.DrugFrequency
import org.simple.clinic.mobius.EffectHandlerTestCase
import org.simple.clinic.teleconsultlog.medicinefrequency.MedicineFrequency
import org.simple.clinic.util.scheduler.TestSchedulersProvider
import org.simple.clinic.uuid.FakeUuidGenerator
import java.util.UUID

class CustomDrugEntryEffectHandlerTest {
  private val uiActions = mock<CustomDrugEntrySheetUiActions>()
  private val prescriptionRepository = mock<PrescriptionRepository>()
  private val facility = TestData.facility(uuid = UUID.fromString("d6685d51-f882-4995-b922-a6c637eed0a5"))
  private val patientUuid = UUID.fromString("be756a9f-005b-4592-8609-209ba0a867a4")

  private val customDrugUUID = UUID.fromString("6bbc5bbe-863c-472a-b962-1fd3198e20d1")
  private val uuidGenerator = FakeUuidGenerator.fixed(customDrugUUID)
  private val drugName = "Amlodipine"

  private val effectHandler = CustomDrugEntryEffectHandler(
      TestSchedulersProvider.trampoline(),
      prescriptionRepository,
      { facility },
      uuidGenerator,
      uiActions).build()

  private val testCase = EffectHandlerTestCase(effectHandler)

  @After
  fun tearDown() {
    testCase.dispose()
  }

  @Test
  fun `when show edit frequency dialog effect is received, show edit frequency dialog`() {
    // given
    val frequency = DrugFrequency.OD

    // when
    testCase.dispatch(ShowEditFrequencyDialog(frequency))

    // then
    testCase.assertNoOutgoingEvents()
    verify(uiActions).showEditFrequencyDialog(frequency)
  }

  @Test
  fun `when set drug frequency effect is received, set drug frequency in the ui`() {
    // given
    val frequency = DrugFrequency.OD

    // when
    testCase.dispatch(SetDrugFrequency(frequency))

    // then
    testCase.assertNoOutgoingEvents()
    verify(uiActions).setDrugFrequency(frequency)
    verifyNoMoreInteractions(uiActions)
  }

  @Test
  fun `when set drug name effect is received with non null values, construct drug name and set it in the ui`() {
    // given
    val dosage = "15mg"
    val frequency = DrugFrequency.OD
    val updatedDrugName = "Amlodipine, 15mg, OD"

    // when
    testCase.dispatch(SetSheetTitle(drugName, dosage, frequency))

    // then
    testCase.assertNoOutgoingEvents()
    verify(uiActions).setSheetTitle(updatedDrugName)
    verifyNoMoreInteractions(uiActions)
  }

  @Test
  fun `when set drug name effect is received with null values, construct drug name and set it in the ui`() {
    // given
    val updatedDrugName = "Amlodipine"

    // when
    testCase.dispatch(SetSheetTitle(drugName, null, null))

    // then
    testCase.assertNoOutgoingEvents()
    verify(uiActions).setSheetTitle(updatedDrugName)
    verifyNoMoreInteractions(uiActions)
  }

  @Test
  fun `when set drug dosage effect is received, set drug dosage in the ui`() {
    // given
    val dosage = "12mg"

    // when
    testCase.dispatch(SetDrugDosage(dosage))

    // then
    testCase.assertNoOutgoingEvents()
    verify(uiActions).setDrugDosage(dosage)
    verifyNoMoreInteractions(uiActions)
  }

  @Test
  fun `when add custom drug to prescription effect is received, add custom drug to prescription repository`() {
    // given
    val dosage = "2.5 mg"
    val frequency = DrugFrequency.OD

    whenever(prescriptionRepository.savePrescription(uuidGenerator.v4(),
        patientUuid,
        drugName,
        dosage,
        null,
        false,
        MedicineFrequency.OD,
        facility)).thenReturn(Completable.complete())

    // when
    testCase.dispatch(SaveCustomDrugToPrescription(patientUuid, drugName, dosage, null, frequency))

    // then
    verify(prescriptionRepository).savePrescription(uuidGenerator.v4(),
        patientUuid,
        drugName,
        dosage,
        null,
        false,
        MedicineFrequency.OD,
        facility)
    verifyNoMoreInteractions(prescriptionRepository)
    testCase.assertOutgoingEvents(CustomDrugSaved)
    verifyZeroInteractions(uiActions)
  }

  @Test
  fun `when update prescription effect is received, then update prescription`() {
    // given
    val dosage = "2.5 mg"
    val frequency = DrugFrequency.OD

    whenever(prescriptionRepository.softDeletePrescription(customDrugUUID)).thenReturn(Completable.complete())

    whenever(prescriptionRepository.savePrescription(
        uuid = customDrugUUID,
        patientUuid = patientUuid,
        name = drugName,
        dosage = dosage,
        rxNormCode = null,
        isProtocolDrug = false,
        frequency = MedicineFrequency.OD,
        facility = facility))
        .thenReturn(Completable.complete())

    // when
    testCase.dispatch(UpdatePrescription(patientUuid, customDrugUUID, drugName, dosage, null, frequency))

    // then
    verify(prescriptionRepository).softDeletePrescription(customDrugUUID)
    verify(prescriptionRepository).savePrescription(
        uuid = customDrugUUID,
        patientUuid = patientUuid,
        name = drugName,
        dosage = dosage,
        rxNormCode = null,
        isProtocolDrug = false,
        frequency = MedicineFrequency.OD,
        facility = facility)
    testCase.assertOutgoingEvents(CustomDrugSaved)
    verifyNoMoreInteractions(prescriptionRepository)
    verifyZeroInteractions(uiActions)
  }

  @Test
  fun `when close custom drug entry sheet effect is received, then close the sheet`() {
    // when
    testCase.dispatch(CloseBottomSheet)

    // then
    verify(uiActions).close()
    verifyNoMoreInteractions(uiActions)
    testCase.assertNoOutgoingEvents()
  }

  @Test
  fun `when fetch prescription effect is received, then fetch prescriptions`() {
    // given
    val prescriptionUuid = UUID.fromString("17f127a7-547d-45c4-a6a8-44a0f182f9c7")
    val prescribedDrug = TestData.prescription(uuid = prescriptionUuid)
    whenever(prescriptionRepository.prescriptionImmediate(prescriptionUuid)).thenReturn(prescribedDrug)

    // when
    testCase.dispatch(FetchPrescription(prescriptionUuid))

    // then
    verify(prescriptionRepository).prescriptionImmediate(prescriptionUuid)
    testCase.assertOutgoingEvents(PrescribedDrugFetched(prescribedDrug))
    verifyZeroInteractions(uiActions)
  }

  @Test
  fun `when remove drug from prescription effect is received, then remove drug entry from prescription`() {
    // given
    whenever(prescriptionRepository.softDeletePrescription(prescriptionUuid = customDrugUUID)).thenReturn(Completable.complete())

    // when
    testCase.dispatch(RemoveDrugFromPrescription(drugUuid = customDrugUUID))

    // then
    verify(prescriptionRepository).softDeletePrescription(customDrugUUID)
    verifyNoMoreInteractions(prescriptionRepository)
    testCase.assertOutgoingEvents(ExistingDrugRemoved)
    verifyZeroInteractions(uiActions)
  }
}