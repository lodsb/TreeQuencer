import de.sciss.synth._
import ugen._
import org.mt4j.output.audio.{Changed1, AudioServer}

SynthDef("icono") {

  /**
   * Returns a parameterised function, that maps a GE to another GE,
   * but mainly a Float to a Float.
   * The function maps a rotation value in degrees to a frequency.
   * 360 degrees get mapped to the 12 halftones.
   *
   * @return GE The result
   */
  def rotationToHalftones(x: GE): GE = {
    440 * 2.pow((x.abs/30).floor/12)
  }


  val gate = "gate".kr(1)
  val volume = "volume".kr(0.083)
  val rotationZ = "rotationZ".kr(0)
  val rotationY = "rotationY".kr(0)
  val rotationX = "rotationX".kr(0)

  val z = rotationToHalftones(rotationZ)

  /**
   * Returns a parameterised function, that maps a GE to another GE,
   * but mainly a Float to a Float.
   * The function is a line, which takes the wanted zeros as parameters (xZero or yZero).
   * Only the absolute value of the function is taken (i.e. always > 0)
   * Before the function calculates anything, it makes the input positive.
   *
   * The function looks like the following: (symmetrical to x-Axis)
   * \              /
   *  \            /
   *   \          /
   *    \________/
   *
   * @param xZero Wanted zero of a function on the x axis
   * @param yZero Wanted zero of a function on the y axis
   * @return GE The result
   */
  def f(xZero: Float = Float.MinValue, yZero: Float = Float.MinValue): GE=>GE = (x: GE) => {

    // input values should at first always be positive
    val greaterZero = x * (0 < x)
    val smallerZero = x * (x < 0) * (-1)
    val z = greaterZero + smallerZero

    var channel: GE = null
    // two functions, but both are the same, just different parameterisation:
    // pass xZero for a linear function, which is a line through the point (xZero,0)
    // pass yZero for a linear function, which is a line through the point (0,yZero)
    if (xZero != Float.MinValue) {
      channel = ((z-360)/(xZero-360))+1
    } else {
      channel = ((1-yZero)*(z/360-1))+1
    }

    // output out should match the following conditions:
    // if out>1   -> out=1
    // if out<0   -> out=0
    // if 0<out<1 -> out
    channel * (0<channel) * (channel<1) + (1<channel)
  }

  def f2(halftones: Float): GE = {
    2f.pow(halftones/12f)
  }

  val (tone1, noiseVol, filterStart, filterEnd, speed, overlap, atk, globAmp) =
    ("tone1".kr(5), "noiseVol".kr(0.005), "filterStart".kr(300), "filterEnd".kr(1000), "speed".kr(1), "overlap".kr(1.5), "atk".kr(5), "globAmp".kr(0.02))

  val (frq1, frq5, frq3, frq7, frq9, frq11, frq13) =
    (z, z*f2(7), z*f2(3), z*f2(10), z*f2(15), z*f2(20), z*f2(24))

  val (amp1, amp5, amp3, amp7, amp9, amp11, amp13) =
    (f(yZero=0.5f)(rotationY), f(xZero=0.0f)(rotationY), f(xZero=40f)(rotationY), f(xZero=80f)(rotationY), f(xZero=120f)(rotationY), f(xZero=160f)(rotationY), f(xZero=200f)(rotationY))


  val gateChanger = Changed1.kr(gate)
  val percussiveEnvelope = EnvGen.kr(Env.perc(attack=speed/atk, release=speed*overlap, level=20), gateChanger, doneAction=1)

	val noiseGen = PinkNoise.ar(noiseVol)
	val line = Line.kr(filterStart,filterEnd,3)

  val w1 = 0.05 * RLPF.ar(Blip.ar(frq1,tone1).madd(amp1*globAmp,0) + noiseGen,line ,0.4) * percussiveEnvelope

  val w2 = 0.05 * RLPF.ar(Blip.ar(frq5,tone1).madd(amp5*globAmp,0) + noiseGen, line ,0.4) * percussiveEnvelope
  val w3 = 0.05 * RLPF.ar(Blip.ar(frq3,tone1).madd(amp3*globAmp,0) + noiseGen, line ,0.4) * percussiveEnvelope
  val w4 = 0.05 * RLPF.ar(Blip.ar(frq7,tone1).madd(amp7*globAmp,0) + noiseGen, line ,0.4) * percussiveEnvelope
  val w5 = 0.05 * RLPF.ar(Blip.ar(frq9,tone1).madd(amp9*globAmp,0) + noiseGen, line ,0.4) * percussiveEnvelope
  val w6 = 0.05 * RLPF.ar(Blip.ar(frq11,tone1).madd(amp11*globAmp,0) + noiseGen, line ,0.4) * percussiveEnvelope
  val w7 = 0.05 * RLPF.ar(Blip.ar(frq13,tone1).madd(amp13*globAmp,0) + noiseGen, line ,0.4) * percussiveEnvelope

  var sin:GE = w1 + w2 + w3 + w4 + w5 + w6 + w7

	val sig = Pan2.ar(SplayAz.ar(2, Limiter.ar(sin*volume)/0.045));


  def scaleDown(x: GE): GE = {
    val a = (x.abs/360) < 0.9
    val b = (x.abs/360) > 0.9
    a*(x.abs/360)+b*0.9
  }
  def scaleMoreDown(x: GE): GE = {
    x.abs/360
  }


  AudioServer.attach(sig)


}
