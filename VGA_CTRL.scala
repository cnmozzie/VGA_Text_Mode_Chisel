import chisel3._

class VGA_CTRL extends Module {
  val io = IO(new Bundle {
    val data = Input(UInt(16.W))
    val addr = Output(UInt(12.W))
    val vga_hs = Output(UInt(1.W))
    val vga_vs = Output(UInt(1.W))
    val vga_rgb = Output(UInt(12.W))
  })

  def HS_A = 96
  def HS_B = 48
  def HS_C = 640
  def HS_D = 16
  def HS_E = 800

  def VS_A = 2
  def VS_B = 33
  def VS_C = 480
  def VS_D = 10
  def VS_E = 525

  val foregroundColor = Module(new ColorDecoder)
  foregroundColor.io.in := io.data(11, 8)
  val backgroundColor = Module(new ColorDecoder)
  backgroundColor.io.in := io.data(14, 12)

  val vga_rom_inst = Module(new vga_rom)
  vga_rom_inst.io.clka := clock
  vga_rom_inst.io.addra := io.data(7, 0)

  val cnt_hs = RegInit(0.U(10.W))
  val cnt_vs = RegInit(0.U(10.W))
  val vga_hs = RegInit(1.U(1.W))
  val vga_vs = RegInit(1.U(1.W))
  val vga_rgb = RegInit(0.U(12.W))

  cnt_hs := Mux(cnt_hs < (HS_E - 1).U, cnt_hs + 1.U, 0.U)
  vga_hs := Mux(cnt_hs < HS_A.U, 0.U, 1.U)
  when (cnt_hs === (HS_E - 1).U) {
      cnt_vs := Mux(cnt_vs < (VS_E - 1).U, cnt_vs + 1.U, 0.U)
  }
  vga_vs := Mux(cnt_vs < VS_A.U, 0.U, 1.U)

  val hs_en = (cnt_hs > (HS_A + HS_B - 1).U) && (cnt_hs < (HS_A + HS_B + HS_C).U)
  val vs_en = (cnt_vs > (VS_A + VS_B - 1).U) && (cnt_vs < (VS_A + VS_B + VS_C).U)

  val res_hs = Mux(cnt_hs > (HS_A + HS_B - 1).U, cnt_hs - (HS_A + HS_B - 1).U, 0.U)
  val res_vs = Mux(cnt_vs > (VS_A + VS_B - 1).U, cnt_vs - (VS_A + VS_B - 1).U, 0.U)

  val row = res_vs >> 4.U
  val col = (res_hs + 2.U) >> 3.U
  val word_cnt = row * 80.U + col

  val char_hs = (res_hs - 2.U)(2, 0)
  val char_vs = res_vs(3, 0)
  val char_mask = ((7.U - char_hs) << 4.U) + (15.U - char_vs)
  
  io.addr := word_cnt

  when (hs_en && vs_en) {
    when (vga_rom_inst.io.douta(char_mask) === 1.U) {
        vga_rgb :=  foregroundColor.io.out
    } otherwise {
        vga_rgb :=  backgroundColor.io.out
    }
  } otherwise {
      vga_rgb := 0.U
  }

  

  io.vga_hs := vga_hs
  io.vga_vs := vga_vs
  io.vga_rgb := vga_rgb
}

class vga_rom extends BlackBox {
  val io = IO(new Bundle {
    val clka = Input(Clock())
    val addra = Input(UInt(8.W))
    val douta = Output(UInt(128.W))
  })
}
