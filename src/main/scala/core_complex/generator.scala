// See LICENSE.SiFive for license details.

package core_complex

import chisel3._
import freechips.rocketchip.config.Parameters
import chisel3.stage.ChiselStage;

object Generator {
    final def main(args: Array[String]) {
        val verilog = ChiselStage.emitSystemVerilog(
            new TestHarness()(Parameters.empty)
        )
    }
}
