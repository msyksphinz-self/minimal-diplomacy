package up_example

import chisel3._
import freechips.rocketchip.config.{Field, Parameters, _}
import freechips.rocketchip.tilelink._
import freechips.rocketchip.diplomacy._

case object AdderBitWidth extends Field[Int]
case object MulBitWidth extends Field[Int]
case object BitWidth extends Field[Int]

class up_example()(implicit p: Parameters) extends Module {
  val adder_params = p.alter((site, here, up) => {
    case BitWidth => up(AdderBitWidth)
  })

  val mul_params = p.alter((site, here, up) => {
    case BitWidth => up(MulBitWidth)
  })

  val io = IO(new Bundle{
    val a = Input(SInt(128.W))
    val b = Input(SInt(128.W))

    val Adder_c = Output(SInt(p(AdderBitWidth).W))
    val mul_c = Output(SInt(p(MulBitWidth).W))
  })

  val Adder_mod = Module(new Adder()(adder_params))
  Adder_mod.io.a := io.a
  Adder_mod.io.b := io.b
  io.Adder_c := Adder_mod.io.c

  val mul_mod = Module(new Mul()(mul_params))
  mul_mod.io.a := io.a
  mul_mod.io.b := io.b
  io.mul_c := mul_mod.io.c
}

class Adder()(implicit p: Parameters) extends Module {
  val bitwidth = p(BitWidth)
  val io = IO(new Bundle{
    val a = Input(SInt(bitwidth.W))
    val b = Input(SInt(bitwidth.W))
    val c = Output(SInt(bitwidth.W))
  })

  io.c := io.a + io.b
}

class Mul()(implicit p: Parameters) extends Module {
  val bitwidth = p(BitWidth)
  val io = IO(new Bundle{
    val a = Input(SInt(bitwidth.W))
    val b = Input(SInt(bitwidth.W))
    val c = Output(SInt((bitwidth * 2).W))
  })

  io.c := io.a * io.b
}

class DefaultConfig() extends Config((site, here, up) => {
  case AdderBitWidth => 64
  case MulBitWidth => 128
})

object main extends App
{
  val p = (new DefaultConfig).toInstance
  chisel3.Driver.emitVerilog(new up_example()(p))
}
