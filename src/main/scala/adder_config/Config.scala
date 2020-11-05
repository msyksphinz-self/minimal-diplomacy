package adder_config

import freechips.rocketchip.config._

class SmallConfig extends Config((site, here, up) => {
  case NumOperands => 2
  case BitWidth => 8
})

class NormalConfig extends Config((site, here, up) => {
  case NumOperands => 4
  case BitWidth => 16
})

class LargeConfig extends Config((site, here, up) => {
  case NumOperands => 16
  case BitWidth => 128
})
