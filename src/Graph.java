
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Daniel J Mathew
 */
public class Graph {
  private Point2D.Double pts[][];
  int num_lines, num_data;
  double origin_x, origin_y, max_y;
  double graph_dimx, graph_dim, scale;
  double unit; //x coordinates of 2 consecutive points are spaced apart by this much
  Canvas canvas;
  Shape x_axis, y_axis;
  String line_text[];
  Color palette[];
  
  //mechanism to flash the printed y-coords on rescale
  int rescale_flash;  //how much longer should coords be recoloured?
  
  /**
   * Moves the graph forward by dropping the points at the rightmost edge.
   * @param steps Number of steps to move forward
   */
  public void MoveForward(int steps)
  {
    for(int s=0; s<steps; s++)
      for(int i=0; i<num_lines; i++)
        for(int j=num_data-1; j>0; j--)
          if(pts[i][j-1] != null) {
            pts[i][j] = pts[i][j-1];
            pts[i][j].x += unit;
          }
  }

  private void Rescale(double change_factor)
  {
    for(int i=0; i<num_lines; i++)
        for(int j=0; j<num_data; j++)
          if(pts[i][j] != null) {
            pts[i][j].y = origin_y - (origin_y - pts[i][j].y) * change_factor;// - 1;
          }
  }

  public void SetScale(double newscale)
  {
    double change_factor = newscale / scale;
//    System.out.println("[Scale change: " + scale +  " to " + newscale + "]");
    scale = newscale;
    max_y = graph_dim / newscale;
    Rescale(change_factor);
    rescale_flash = 2;
  }

  //(x,y): the new point
  //r,c  : row and column; column = 0 always as the new point comes from the 
  // left always
  public void SetPoint(int x, int y, int r, int c)
  {
    double newscale;
    //auto-scaling
    if(Math.abs(y) > max_y) {
      max_y = (int) (Math.ceil(Math.abs(y) / graph_dim) * graph_dim);
      newscale = graph_dim / max_y;
      SetScale(newscale);
    }
    pts[r][c] = ConvertPoint(x, y);
  }

  public void EraseLine(int l)
  {
//    System.out.print("Erasing line #" + l + ":");
    for(int i=num_data-1; i>0; i--)
      if(pts[l][i] != null && pts[l][i-1] != null) {
//        System.out.print(PointToString(pts[l][i]) + " to " +
//                PointToString(pts[l][i-1]) + " ");
        canvas.eraseOutline(new Line2D.Double(pts[l][i], pts[l][i-1]));
        canvas.erase(new Line2D.Double(pts[l][i], pts[l][i-1]));
      }
//    System.out.println("");
  }

  public void DrawLine(int l)
  {
//    System.out.print("Drawing line #" + l + ":");
    canvas.setForegroundColour(Color.BLACK);
    for(int i=num_data-1; i>0; i--)
      if(pts[l][i] != null && pts[l][i-1] != null) {
//        System.out.print(PointToString(pts[l][i]) + " to " +
//                PointToString(pts[l][i-1]) + " ");
        canvas.setForegroundColour(palette[l]);
        canvas.drawLine(pts[l][i], pts[l][i-1]);
      }
//    System.out.println("");
  }

  /**
   * Converts a given point into a Point2D object in the coordinate system
   * followed by this graph object.
   * @param x X-coordinate of point
   * @param y Y-coordinate of point
   * @return
   */
  public Point2D.Double ConvertPoint(int x, int y)
  {
    double x_new = x * scale + origin_x;
    double y_new = origin_y - y * scale; //- 1;  // -1 so that x axis won't be touched
    Point2D.Double p;
//    System.out.print("Converted (" + x + "," + y + ") to ");
    if(x_new >= origin_x && x_new <= origin_x + graph_dimx && y_new <= origin_y + graph_dim
            && y_new >= origin_y - graph_dim) {
      p = new Point2D.Double(x_new, y_new);
//      System.out.println(PointToString(p));
      return p;
    }
    else {
//      System.out.println("null");
      //TODO: handle this case more gracefully
      return null;
    }
  }

  private String PointToString(Point2D.Double p)
  {
    return "[" + (int)p.x + "," + (int)p.y + "]";
  }

  @Override
  public String toString()
  {
    String s = new String();
    int old_len = 0;
    for(int i=0; i<num_lines; i++) {
      for(int j=0; j<num_data; j++) {
        if(pts[i][j] != null)
          s += PointToString(pts[i][j]) + " ";
      }
      if(s.length() > old_len) {
        s += "\n";
        old_len = s.length();
      }
    }
    return s;
  }

  /* Draw: redraws the co-ordinate axes, labels and legend. */
  public void Draw()
  {
    Double num_y = 4.0; //number of coords printed on +y-axis 
    if(rescale_flash > 0) {
        canvas.setForegroundColour(Color.RED);
        rescale_flash--;
    }
    else
        canvas.setForegroundColour(Color.BLACK);
    canvas.draw(x_axis);
    canvas.draw(y_axis);
    this.canvas.drawString("t", (int) (origin_x + graph_dimx - 5),
            (int) (origin_y + 15));
    //this.canvas.drawString("0.0", (int)(origin_x - 55), (int)(origin_y + 5));
    
    //Draw y-coordinates
    for(int i=0; i<=num_y; i++) {
        this.canvas.drawString(Double.toString(max_y * i/num_y), (int)(origin_x - 55),
            (int)(origin_y - graph_dim * i/num_y + 5));   //+y-axis
        if(i != 0) 
            this.canvas.drawString(Double.toString(-max_y * i/num_y), (int)(origin_x - 55),
                (int)(origin_y + graph_dim * i/num_y + 5));   //-y-axis
    }

    //Draw legend
    for(int i=0; i<num_lines; i++) {
      this.canvas.setForegroundColour(palette[i]);
      this.canvas.drawString(line_text[i], (int)(origin_x + graph_dimx + 10),
              (int)(origin_y - graph_dim + 15*i));
    }
  }

  public Graph(int num_lines, int num_data, double origin_x, double origin_y,
          double graph_dim, double scale, String[] legend, Canvas canvas)
  {
    this.num_lines = num_lines;
    this.num_data = num_data;
    pts = new Point2D.Double[num_lines][num_data];
    this.origin_x = origin_x;
    this.origin_y = origin_y/2;
    this.graph_dim = graph_dim;
    this.graph_dimx = graph_dim * 2;
    this.canvas = canvas;
    x_axis = new Line2D.Double(this.origin_x-2, this.origin_y+2, 
            this.origin_x + graph_dimx, this.origin_y+2);
    y_axis = new Line2D.Double(this.origin_x-2, this.origin_y+2 + graph_dim, 
            this.origin_x-2, this.origin_y - graph_dim);
    this.scale = scale;
    this.max_y = this.graph_dim / this.scale;
    unit = graph_dimx / num_data;
    line_text = new String[num_lines];
    line_text = legend;
    
    this.rescale_flash = 0;

    palette = new java.awt.Color[10];
    palette[0] = Color.BLACK;
    palette[1] = Color.GREEN;
    palette[2] = Color.LIGHT_GRAY;
    palette[3] = Color.MAGENTA;
    palette[4] = Color.ORANGE;
    palette[5] = Color.RED;

//    for(int i=0; i<num_lines; i++) {
//      this.canvas.setForegroundColour(line_colour[i]);
//      this.canvas.drawString(line_text[i], (int)(origin_x + graph_dim + 10),
//              (int)(origin_y - graph_dim + 15*i));
//    }
  }
}
