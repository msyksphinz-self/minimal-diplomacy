package core_complex

import chisel3._

import freechips.rocketchip.config._
import freechips.rocketchip.tilelink._
import freechips.rocketchip.diplomacy._

class core_complex(ramBeatBytes: Int, txns: Int)(implicit p: Parameters) extends LazyModule {
  val pusher1 = LazyModule(new TLPatternPusher("pat1", Seq(
    new WritePattern(0x100, 0x2, 0x012345678L),
    new WritePattern(0x500, 0x2, 0x0abcdef01L),
    new ReadExpectPattern(0x100, 0x2, 0x012345678L),
    new ReadExpectPattern(0x500, 0x2, 0x0abcdef01L)
  )))

  val ifu    = LazyModule(new ifu("ifu"))

  val xbar   = LazyModule(new TLXbar)
  val memory = LazyModule(new TLRAM(AddressSet(0x02000, 0x0fff), beatBytes = ramBeatBytes))

  xbar.node := pusher1.node
  xbar.node := ifu.node
  memory.node := xbar.node

  lazy val module = new LazyModuleImp(this) {
    val io = IO(new Bundle {
      val finished = Output(Bool())
    })
    pusher1.module.io.run := true.B
    // pusher2.module.io.run := true.B
    io.finished := pusher1.module.io.done
  }
}
