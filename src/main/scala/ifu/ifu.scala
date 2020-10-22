package freechips.rocketchip.tilelink

import chisel3._

import freechips.rocketchip.config._
import freechips.rocketchip.amba.ahb._
import freechips.rocketchip.amba.apb._
import freechips.rocketchip.amba.axi4._
import freechips.rocketchip.subsystem.{BaseSubsystemConfig}
import freechips.rocketchip.devices.tilelink._
import freechips.rocketchip.tilelink._
import freechips.rocketchip.util._
import freechips.rocketchip.diplomacy._
import freechips.rocketchip.diplomaticobjectmodel.logicaltree.{BusMemoryLogicalTreeNode, LogicalModuleTree, LogicalTreeNode}
import freechips.rocketchip.diplomaticobjectmodel.model.{OMECC, TL_UL}

class ifu(name: String)(implicit p: Parameters) extends LazyModule {
  val node = TLClientNode(Seq(TLClientPortParameters(Seq(TLClientParameters(name = name)))))

  lazy val module = new LazyModuleImp(this) {
    val io = IO(new Bundle {
      val run   = Input(Bool())
      val done  = Output(Bool())
      val error = Output(Bool())
    })

    val (out, edge) = node.out(0)

    val baseEnd = 0
    val (sizeEnd,   sizeOff)   = (edge.bundle.sizeBits   + baseEnd, baseEnd)
    val (sourceEnd, sourceOff) = (edge.bundle.sourceBits + sizeEnd, sizeEnd)
    val beatBytes = edge.bundle.dataBits

    val counter = RegInit(0.U((beatBytes * 8).W))

    val a = out.a
    val d = out.d

    val a_addr = RegInit(0.U(edge.bundle.addressBits.W))

    val req_counter  = RegInit(0.U(5.W))
    val ack_counter = RegInit(0.U(5.W))
    val finish = RegInit(false.B)

    when (a.fire()) {
      req_counter := req_counter + 1.U
      when (req_counter(3, 0).andR) {
        a_addr := 0.U
      } .otherwise {
        a_addr := a_addr + beatBytes.U
      }
    }
    val r_error = Reg(Bool())
    r_error := false.B
    io.error := r_error
    val answer = 0xdead0000L.U | (ack_counter(3, 0) ## 0.U(5.W))
    dontTouch(answer)

    when (d.fire()) {
      ack_counter := ack_counter + 1.U
      when(ack_counter(4) && (d.bits.data =/= answer)) {
        r_error := true.B
      }
    }
    when (ack_counter.andR && a.fire()) {
      finish := true.B
    }

    val (_, get_pbits) = edge.Get(0.U , a_addr, 2.U)
    val (_, put_pbits) = edge.Put(0.U , a_addr, 2.U, 0xdead0000L.U | a_addr)
    a.valid := io.run && !finish
    a.bits := Mux(req_counter(4), get_pbits, put_pbits)

    d.ready := true.B

    io.done := finish

    // Tie off unused channels
    out.b.valid := false.B
    out.c.ready := true.B
    out.e.ready := true.B
  }
}


object ifu
{
  def apply(name: String)(implicit p: Parameters): TLInwardNode =
  {
    val pusher = LazyModule(new ifu(name))
    pusher.node
  }
}
