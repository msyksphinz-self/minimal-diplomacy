// See LICENSE.SiFive for license details.

package core_complex

import chisel3._
import freechips.rocketchip.config.Parameters
import chisel3.Driver;

object Generator {
    final def main(args: Array[String]) {
        val verilog = Driver.emitVerilog(
            new TestHarness()(Parameters.empty)
        )
    }
}
