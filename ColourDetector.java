import java.awt.Color;
import java.math.*;

//Determines the colours of a rubix cube from a picture of it by sampling the colours in the center of each square
public class ColourDetector {

    int face[][];
    Color colours[];

    public ColourDetector()
    {
        face = new int[3][3];

        //Sets up an array of each of the colours on a Rubix cube
        colours = new Color[6];
        colours[0] = new Color(190,30,20);//Red - 0 
        colours[1] = new Color(0,40,245);//Blue - 1
        colours[2] = new Color(80,180,30);//Green - 2
        colours[3] = new Color(170,80,30);//Orange - 3
        colours[4] = new Color(175,150,30);//Yellow - 4
        colours[5] = new Color(175,175,175);//White - 5
    }

    public int findColour(Color current)
    {
        int cr = current.getRed();
        int cg = current.getGreen();
        int cb = current.getBlue();

        double min = 1000.0;
        int foundColour = 0;

        //Loop through each of the colours in the colours array
        for (int i = 0; i < 6; i++)
        {
            int tr = colours[i].getRed();
            int tg = colours[i].getGreen();
            int tb = colours[i].getBlue();

            //Compute the Pythagorean distance between the 2 colours
            double variance = Math.sqrt(Math.pow(cr-tr,2) + Math.pow(cg-tg,2) + Math.pow(cb-tb,2));

            //foundColour holds the colour index with smallest distance (ie closest match to current)
            if(variance < min)
            {
                min = variance;
                foundColour = i;
            }
        }
        return foundColour;
    }

    //Checks if the current pixel is black, returning 1 if it is.
    public int checkBlack(Color current)
    {
        Color black = new Color(0,0,0);
        int r = current.getRed();
        int g = current.getGreen();
        int b = current.getBlue();

        int variance = 50;

        //Determines if the current pixel is within a certain range of the base black colour
        if(black.getRed()-variance <= r && r <=black.getRed()+variance &&
           black.getGreen()-variance <= g && r <= black.getGreen()+variance &&
           black.getBlue()-variance <= b && b <= black.getBlue()+variance)
        {
            return 1;
        }

        return 0;
    }

    //Used to print the current face grid for debugging
    public void printFace()
    {
        for (int j = 0; j < 3; j++)
        {
            for (int i = 0; i < 3; i++)
            {
                System.out.print(face[j][i] + " ");
            }
            System.out.println();
        }
    }

    public int[][] run(String filename)
    {
        Picture pic = new Picture(filename);

        //determine the x,y coords of the top left and bottom right of the cube
        int firstBlackX = pic.width(); //top left x
        int firstBlackY = pic.height(); //top left y

        int lastBlackX = 0; //bottom right x
        int lastBlackY = 0; //bottom right y

        for (int j = 0; j < pic.height(); j++) {
            for (int i = 0; i < pic.width(); i++) {
                if (checkBlack(pic.get(i,j)) == 1) //Black detected
                {
                    if(i < firstBlackX)
                    {
                        firstBlackX = i;
                    }
                    if(j < firstBlackY)
                    {
                        firstBlackY = j;
                    }

                    if(i > lastBlackX)
                    {
                        lastBlackX = i;
                    }
                    if(j > lastBlackY)
                    {
                        lastBlackY = j;
                    }
                }

            }
        }

        int cubeWidth = lastBlackX - firstBlackX + 1;
        int cubeHeight = lastBlackY - firstBlackY + 1;

        int sqWidth = (int)Math.round(cubeWidth / 3); //Width of each square on the cube
        int sqOffsetWidth = (int)Math.round(sqWidth / 2); //Offset used to select the middle of each square

        int sqHeight = (int)Math.round(cubeHeight / 3); //Height of each square on the cube
        int sqOffsetHeight = (int)Math.round(sqHeight / 2); //Offset used to select the middle of each square

        //Loops through the center of each of the squares on the face, determining which colour a square is and
        //setting the face array to hold this.
        for (int j = 0; j < 3; j++)
        {
            for (int i = 0; i < 3; i++)
            {
                int x = firstBlackX + (i*sqWidth) + sqOffsetWidth;
                int y = firstBlackY + (j*sqHeight) + sqOffsetHeight;

                int colour = findColour(pic.get(x,y));

                face[j][i] = colour;
            }
        }
        printFace();

        return face;

    }
	
    public static void main(String[] args)
    {
        ColourDetector cube = new ColourDetector();
        int balls[][] = cube.run("img/side1.jpg");
    }
}