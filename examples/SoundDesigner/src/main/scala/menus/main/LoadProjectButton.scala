package ui.menus.main

import org.mt4j.Application

import org.mt4j.components.TransformSpace
import org.mt4j.components.MTComponent
import org.mt4j.components.visibleComponents.shapes.MTEllipse
import org.mt4j.util.MT4jSettings

import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor 
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent
import org.mt4j.input.inputProcessors.IGestureEventListener
import org.mt4j.input.inputProcessors.MTGestureEvent

import org.mt4j.util.Color
import org.mt4j.util.math.Vector3D
import org.mt4j.util.math.Vertex
import org.mt4j.types.{Vec3d}

import processing.opengl.PGraphicsOpenGL
import processing.core.PConstants._

import ui.menus._
import ui.paths.types._
import ui._
import ui.persistence._

object LoadProjectButton {

  def apply(app: Application, menu: Menu, center: Vector3D) = {
      new LoadProjectButton(app, menu, center)
  }  
 
  
}


class LoadProjectButton(app: Application, menu: Menu, center: Vector3D) extends Button(app, menu, center) {

  //this.setTexture(Ui.loadImage(MT4jSettings.getInstance.getDefaultImagesPath + "load.png"))
  
  override def drawComponent(g: PGraphicsOpenGL) = {
    super.drawComponent(g)
    val center = this.getCenterPointLocal()
    val cx = center.getX()
    val cy = center.getY()              
    val r = 0.5f * this.radius
    g.noStroke()
    g.fill(this.itemForegroundColor.getR, this.itemForegroundColor.getG, this.itemForegroundColor.getB, this.itemForegroundColor.getA * this.opacity)
    
    g.beginShape()
    g.vertex(cx - 2/3f*r, cy + r)
    g.vertex(cx - 2/3f*r, cy)    
    g.vertex(cx - 1/4f*r, cy)   
    g.vertex(cx - 1/4f*r, cy - 1/4f*r)      
    g.vertex(cx - 1/2f*r, cy - 1/4f*r) 
    g.vertex(cx, cy - r)       
    g.vertex(cx + 1/2f*r, cy - 1/4f*r)    
    g.vertex(cx + 1/4f*r, cy - 1/4f*r)   
    g.vertex(cx + 1/4f*r, cy)    
    g.vertex(cx + 2/3f*r, cy)    
    g.vertex(cx + 2/3f*r, cy + r)    
    g.endShape(CLOSE)
    
  }  
  
  override def clicked() = {
    super.clicked()
    ProjectFileLoader.load()
  }
  
  override def up() = {
    super.up()
  }
  
  override def down() = {
    super.down() 
  }
  
}
