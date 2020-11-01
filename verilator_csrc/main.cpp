#include "VTestHarness.h"
#include "verilated.h"
// #include "verilated_vcd_c.h"
#include "verilated_fst_c.h"

static uint64_t trace_count = 0;
bool done_reset;
bool verbose;

double sc_time_stamp()
{
  return trace_count;
}

int main(int argc, char **argv, char **env) {
  Verilated::commandArgs(argc, argv);
  VTestHarness* top = new VTestHarness;

  Verilated::traceEverOn(true);
  // VerilatedVcdC* tfp = new VerilatedVcdC;
  VerilatedFstC* tfp = new VerilatedFstC;

  vluint64_t sim_time = 200000;

  top->trace(tfp, 99);  // Trace 99 levels of hierarchy
  tfp->open("simx.fst");

  top->clock = 0;
  top->reset = 1;

  while (sc_time_stamp() < sim_time && !top->io_success) {
    if (trace_count >= 100) {
      top->reset = 0;
    }
    if ((trace_count % 5) == 0) {
      top->clock = !top->clock;
    }

    trace_count ++;
    top->eval();
    tfp->dump(trace_count);
    if ((trace_count % 1000) == 0) {
      printf ("Count = %u\n", trace_count);
    }
    if (done_reset) {
      break;
    }
  }
  tfp->close();

  delete top;
  exit(0);
}
