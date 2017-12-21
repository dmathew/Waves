
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.NoSuchElementException;
import java.util.Scanner;


/**
 *
 * @author Daniel J Mathew
 */
public class Main {

  /**
    * @param args the command line arguments
   */
  public static void main(String[] args) throws FileNotFoundException, IOException, NoSuchElementException
  {
    if(args.length < 4) {
      System.out.println("Usage: waves input_file n_streams time_res stream_name ...");
      System.exit(0);
    }

    int n_streams = Integer.parseInt(args[1]);
    int i, pindex, x, y, span = 40;
    int time_res = Integer.parseInt(args[2]);
    String[] legend = new String[n_streams];
    double origin_x = 70.0, origin_y = 550.0, graph_dim = 250.0, scale = 2.0;
    boolean data_start = false;
    String inp;
    String inp_arr[] = new String[n_streams];
    Point2D.Double p;
    BufferedReader br = null;
    Scanner sc = null;
    Canvas canvas = new Canvas("waves", 650, 600, Color.white);
    canvas.setVisible(true);
    for(i=0; i<n_streams; i++)
      legend[i] = args[3 + i];

    Graph graph = new Graph(n_streams, span, origin_x, origin_y, graph_dim, 
            scale, legend, canvas);
    
    if(args[0].equals("-"))
      br = new BufferedReader(new InputStreamReader(System.in));
    else
      sc = new java.util.Scanner(new java.io.File(args[0]));

    x = 2;
    while(true) {
      inp = "";
      if(sc != null)
        try {
          inp = sc.nextLine();
        }
        catch(Exception e) {
          System.exit(0);
        }
      else
        inp = br.readLine();
      if(inp != null)
          System.out.println(inp);
      if(inp != null && inp.indexOf("**SOData**") != -1) { //Start-of-Data marker
        data_start = true;
        continue;
      }
      else if(!data_start)
          continue;
//      System.out.print(inp + " " + scale + " ");
      inp_arr = inp.split(" ");
      graph.canvas.erase();
      graph.Draw();
      for(pindex=0,i=0; i<inp_arr.length; i++) {
        if(inp_arr[i].equals("") || inp_arr[i].equals("[java]")) //happens if there are extra spaces in input file
          continue;
        y = (int)Float.parseFloat(inp_arr[i]);
        //y = Integer.parseInt(inp_arr[i]);
        
        //p = graph.ConvertPoint(x,y);
//        if(p != null)
//          System.out.print("-> " + p.y + " ");
//        else
//          System.out.println("p null");
        graph.SetPoint(x, y, pindex, 0);
        graph.DrawLine(pindex);
        pindex++;
        if(pindex == n_streams) break;
      }
//      System.out.println("");
      canvas.wait(time_res);
      //System.out.println("Graph now:\n" + graph.toString());
      //for(i=0; i<n_streams; i++)
      //  graph.EraseLine(i);
      
      graph.MoveForward(1);
    }

  } // End of main()

}
