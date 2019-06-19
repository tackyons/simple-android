package org.simple.clinic.allpatientsinfacility

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class TrampolineSchedulersProvider : SchedulersProvider {
  override fun io(): Scheduler = Schedulers.trampoline()
  override fun computation(): Scheduler = Schedulers.trampoline()
  override fun ui(): Scheduler = Schedulers.trampoline()
}
