package org.simple.clinic.home.overdue.search

import java.time.LocalDate
import java.util.UUID

sealed class OverdueSearchEffect

object LoadOverdueSearchHistory : OverdueSearchEffect()

data class ValidateOverdueSearchQuery(val searchQuery: String) : OverdueSearchEffect()

data class AddQueryToOverdueSearchHistory(val searchQuery: String) : OverdueSearchEffect()

data class SearchOverduePatients(val searchQuery: String, val since: LocalDate) : OverdueSearchEffect()

sealed class OverdueSearchViewEffect : OverdueSearchEffect()

data class OpenPatientSummary(val patientUuid: UUID) : OverdueSearchViewEffect()

data class OpenContactPatientSheet(val patientUuid: UUID) : OverdueSearchViewEffect()
