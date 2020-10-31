package core_complex

import chisel3._
import freechips.rocketchip.config.{Field, Parameters, _}
import freechips.rocketchip.tilelink._
import freechips.rocketchip.diplomacy._
import freechips.rocketchip.util.ElaborationArtefacts

case object BusWidthBytes extends Field[Int]
case object ConnectIfu extends Field[Boolean]
case object AddrSize extends Field[Int]

class core_complex(implicit p: Parameters) extends LazyModule {

  val xbar   = LazyModule(new TLXbar)
  val memory = LazyModule(new TLRAM(AddressSet(0x0, p(AddrSize)-1), beatBytes = p(BusWidthBytes)))

  val pusher1 = LazyModule(new TLPatternPusher("pat" + i.toString(), Seq(
    new WritePattern(0x100, 0x2, 0x012345678L),
    new WritePattern(0x500, 0x2, 0x0abcdef01L),
    new ReadExpectPattern(0x100, 0x2, 0x012345678L),
    new ReadExpectPattern(0x500, 0x2, 0x0abcdef01L)
  )))
  xbar.node := pusher1.node

  if (p(ConnectIfu)) {
    val ifu    = LazyModule(new ifu("ifu"))
    xbar.node := ifu.node
  }
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
