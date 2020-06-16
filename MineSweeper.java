import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JFrame;
import java.awt.Graphics;
import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.SwingUtilities;
import java.util.Random;

public class MineSweeper
{
	public static class Tile 
	{
		private int cont, x, y, inc;
		private boolean vis, flagged;
		
		
		public Tile(int cont, int x, int y, int inc)
		{
			this.cont = cont;
			this.x = x;
			this.y = y;
			this.inc = inc;
			vis = false;
		}
		
		public void draw(Graphics g)
		{
			if(vis)
			{
				if (cont == -1)
				{
					g.setColor(new Color(255, 0, 0));
					g.fillRect(x, y, inc, inc);
					g.setColor(new Color(0, 0, 0));
					g.drawString("@", x + (inc / 2 - inc / 5), y + (inc / 2 + inc / 4));
				}
				else if(cont == 0)
				{
					g.setColor(new Color(255, 255, 255));
					g.fillRect(x, y, inc, inc);
				}
				else
				{
					g.setColor(new Color(255, 255, 255));
					g.fillRect(x, y, inc, inc);
					switch(cont)
					{
						case 1:
							g.setColor(new Color(0, 0, 255));
							break;
						case 2:
							g.setColor(new Color(0, 255, 0));
							break;
						case 3:
							g.setColor(new Color(255, 0, 0));
							break;
						case 4:
							g.setColor(new Color(0, 0, 150));
							break;
						case 5:
							g.setColor(new Color(128, 0, 0));
							break;
						case 6:
							g.setColor(new Color(0, 128, 128));
							break;
						case 7:
							g.setColor(new Color(0, 0, 0));
							break;
						case 8:
							g.setColor(new Color(128, 128, 128));
							break;
						default:
							g.setColor(new Color(0, 0, 0));
							break;
					}
					g.drawString("" + cont, x + (inc / 2 - inc / 5), y + (inc / 2 + inc / 4));
				}
			}
			else if(flagged)
			{
				g.setColor(new Color(0, 0, 0));
				g.fillRect(x, y, inc, inc);
				g.setColor(new Color(255, 0, 0));
				g.drawString("F", x + (inc / 2 - inc / 5), y + (inc / 2 + inc / 4));
			}
			else if(cont == -2)
			{
				g.setColor(new Color(128, 128, 128));
				g.fillRect(x, y, inc, inc);
			}
			else
			{
				g.setColor(new Color(0, 0, 0));
				g.fillRect(x, y, inc, inc);
			}
		}
		
		public void open()
		{
			if(cont != -2)
			{
				vis = true;
			}
		}
		
		public boolean isOpen()
		{
			return vis;
		}
		
		public void flag()
		{
			if(cont != -2)
			{
				flagged = !flagged;
			}
		}
		
		public boolean isFlagged()
		{
			return flagged;
		}
		
		public int getCont()
		{
			return cont;
		}
		
		public void setCont(int n)
		{
			cont = n;
		}
	}
	
	public static class Cord
	{
		public int x, y;
		
		public Cord(int x, int y)
		{
			this.x = x;
			this.y = y;
		}
	}
	
	public static abstract class InfoBox <T , P extends JPanel>
	{
		protected int x, y;
		protected T info;
		protected P parent;
		
		public InfoBox(int x, int y, P parent)
		{
			this.x = x;
			this.y = y;
			this.parent = parent;
		}
		
		public void draw(Graphics g)
		{
			g.setColor(new Color(0, 0, 0));
			g.fillRect(x, y, 50, 20);
			g.setColor(new Color(255, 0, 0));
			g.drawString("" + info, x + 10 , y + 15);
		}
	}
	
	public static class Timer <T extends JPanel> extends InfoBox<Integer, T> implements Runnable
	{
		private int time;
		private Thread t;
		private T daddy;
		private boolean running;
		
		public Timer(int x, int y, T parent)
		{
			super(x, y, parent);
			info = 0;
			running = true;
			t = new Thread(this, "Timer");
			t.start();
		}
		
		public void run()
		{
			do
			{
				try
				{
					Thread.sleep(1000);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				info++;
				parent.repaint();
			}
			while(running);
		}
		
		public int stop()
		{
			running = false;
			return time;
		}
	}
	
	public static class MinesLeft <T extends JPanel> extends InfoBox<Integer, T>
	{
		public MinesLeft(int mines, int x, int y, T parent)
		{
			super(x, y, parent);
			info = mines;
		}
		
		public void sub()
		{
			info--;
			parent.repaint();
		}
		
		public void add()
		{
			info++;
			parent.repaint();
		}
	}
	
	public static class Game extends JPanel implements MouseListener
	{
		private Tile[][] tiles;
		private int xOffset, yOffset, tWidth, tHeight, inc, seed, numMines;
		private Random r;
		private boolean first;
		private Timer <Game> timer;
		private MinesLeft <Game> minesleft;

		public Game()
		{
			super();
			addMouseListener(this);
			seed = 410;
			inc = 16;
			xOffset = 100;
			yOffset = 100;
			tWidth = 16;
			tHeight = 16;
			numMines = 50;
			tWidth += 2;
			tHeight += 2;
			numMines += 1;
			first = true;
			r = new Random();
			minesleft = new MinesLeft <Game>(numMines,xOffset, yOffset - inc * 2, this);
			timer = new Timer <Game>(xOffset + (inc * tWidth) - 3 * inc, yOffset - inc * 2, this);
			tiles = new Tile[tWidth][tHeight];
			for(int i = 0; i < tWidth; i++)
			{
				for(int j = 0; j < tHeight; j++)
				{
					tiles[i][j] = new Tile(0, xOffset + i * inc, yOffset + j * inc, inc);
				}
			}
			for(int i = 0; i < tWidth; i++)
			{
				tiles[i][0].setCont(-2);
				tiles[i][tWidth - 1].setCont(-2);
			}
			for(int i = 0; i < tHeight; i++)
			{
				tiles[0][i].setCont(-2);
				tiles[tHeight - 1][i].setCont(-2);
			}
		}
		
		public void init(int x, int y)
		{
			int randX, randY, c = 0;
			do
			{
				for(int i = 0; i < tWidth; i++)
				{
					for(int j = 0; j < tHeight; j++)
					{
						tiles[i][j] = new Tile(0, xOffset + i * inc, yOffset + j * inc, inc);
					}
				}
				for(int i = 0; i < tWidth; i++)
				{
					tiles[i][0].setCont(-2);
					tiles[i][tWidth - 1].setCont(-2);
				}
				for(int i = 0; i < tHeight; i++)
				{
					tiles[0][i].setCont(-2);
					tiles[tHeight - 1][i].setCont(-2);
				}
				for(int i = 0; i < numMines; i++)
				{
					do
					{
						randX = r.nextInt(tWidth - 1);
						randY = r.nextInt(tHeight - 1);
						if(tiles[randX][randY].getCont() == 0)
						{
							tiles[randX][randY].setCont(-1);
						}
					}
					while(tiles[randX][randY].getCont() == 0);
				}
				for(int i = 1; i < tWidth - 1; i++)
				{
					for(int j = 1; j < tHeight - 1; j++)
					{
						if(tiles[i][j].getCont() != -1)
						{
							tiles[i][j].setCont(Danger(tiles, i, j));
						}
					}
				}
				c++;
				System.out.println(c + " " + tiles[x][y].getCont());
			}
			while(tiles[x][y].getCont() != 0 && c < 10000);
		}
		
		private int Danger(Tile[][] tiles, int x, int y)
		{
			int rv = 0;
			if(tiles[x + 1][y + 1].getCont() == -1)
			{
				rv++;
			}
			if(tiles[x + 1][y].getCont() == -1)
			{
				rv++;
			}
			if(tiles[x + 1][y - 1].getCont() == -1)
			{
				rv++;
			}
			if(tiles[x][y - 1].getCont() == -1)
			{
				rv++;
			}
			if(tiles[x - 1][y - 1].getCont() == -1)
			{
				rv++;
			}
			if(tiles[x - 1][y].getCont() == -1)
			{
				rv++;
			}
			if(tiles[x - 1][y + 1].getCont() == -1)
			{
				rv++;
			}
			if(tiles[x][y + 1].getCont() == -1)
			{
				rv++;
			}
			return rv;
		}
		
		private Cord[] addXY(Cord[] to, int x, int y)
		{
			Cord[] temp = new Cord[to.length];
			for(int i = 0; i < temp.length; i++)
			{
				temp[i] = to[i];
			}
			to = new Cord[to.length + 1];
			for(int i = 0; i < temp.length; i++)
			{
				to[i] = temp[i];
			}
			to[to.length - 1] = new Cord(x, y);
			return to;
		}
		
		private void massOpen(int ix, int iy)
		{
			int x, y;
			Cord[] to = new Cord[0];
			to = addXY(to, ix, iy);
			for(int i = 0; i < to.length; i++)
			{
				x = to[i].x;
				y = to[i].y;
				if(tiles[x][y].getCont() == 0)
				{
					if(!tiles[x + 1][y + 1].isFlagged() && !tiles[x + 1][y + 1].isOpen()) // + +
					{
						tiles[x + 1][y + 1].open();
						if(tiles[x + 1][y + 1].getCont() == 0)
						{
							to = addXY(to, x + 1, y + 1);
						}
					}
					if(!tiles[x - 1][y - 1].isFlagged() && !tiles[x - 1][y - 1].isOpen()) // - -
					{
						tiles[x - 1][y - 1].open();
						if(tiles[x - 1][y - 1].getCont() == 0)
						{
							to = addXY(to, x - 1, y - 1);
						}
					}
					if(!tiles[x + 1][y - 1].isFlagged() && !tiles[x + 1][y - 1].isOpen()) //+ -
					{
						tiles[x + 1][y - 1].open();
						if(tiles[x + 1][y - 1].getCont() == 0)
						{
							to = addXY(to, x + 1, y - 1);
						}
					}
					if(!tiles[x - 1][y + 1].isFlagged() && !tiles[x - 1][y + 1].isOpen()) //- +
					{
						tiles[x - 1][y + 1].open();
						if(tiles[x - 1][y + 1].getCont() == 0)
						{
							to = addXY(to, x - 1, y + 1);
						}
					}
					if(!tiles[x][y + 1].isFlagged() && !tiles[x][y + 1].isOpen()) //n +
					{
						tiles[x][y + 1].open();
						if(tiles[x][y + 1].getCont() == 0)
						{
							to = addXY(to, x, y + 1);
						}
					}
					if(!tiles[x][y - 1].isFlagged() && !tiles[x][y - 1].isOpen()) //n -
					{
						tiles[x][y - 1].open();
						if(tiles[x][y - 1].getCont() == 0)
						{
							to = addXY(to, x, y - 1);
						}
					}
					if(!tiles[x + 1][y].isFlagged() && !tiles[x + 1][y].isOpen()) //+ n
					{
						tiles[x + 1][y].open();
						if(tiles[x + 1][y].getCont() == 0)
						{
							to = addXY(to, x + 1, y);
						}
					}
					if(!tiles[x - 1][y].isFlagged() && !tiles[x - 1][y].isOpen()) //- n
					{
						tiles[x - 1][y].open();
						if(tiles[x - 1][y].getCont() == 0)
						{
							to = addXY(to, x - 1, y);
						}
					}
				}
			}
		}
		
		private void endGame()
		{
			for(int i = 0; i < tWidth; i++)
			{
				for(int j = 0; j < tHeight; j++)
				{
					timer.stop();
					tiles[i][j].open();
				}
			}
		}
		
		private void click(MouseEvent e)
		{
			int x = e.getX(), y = e.getY(), tx = -1, ty = -1;
			for(int i = 0; i < tWidth; i++)
			{
				if(x >= xOffset + i * inc && x <= xOffset + (i + 1) * inc)
				{
					tx = i;
					break;
				}
			}
			for(int i = 0; i < tHeight; i++)
			{
				if(y >= yOffset + i * inc && y <= yOffset + (i + 1) * inc)
				{
					ty = i;
					break;
				}
			}
			if(SwingUtilities.isRightMouseButton(e))
			{
				tiles[tx][ty].flag();
				if(tiles[tx][ty].isFlagged())
				{
					minesleft.sub();
				}
				else
				{
					minesleft.add();
				}
			}
			if(SwingUtilities.isLeftMouseButton(e))
			{
				if(first)
				{
					init(tx, ty);
					first = false;
				}
				if (!tiles[tx][ty].isFlagged())
				{
					tiles[tx][ty].open();
					if(tiles[tx][ty].getCont() == -1)
					{
						endGame();
					}
					else
					{
						massOpen(tx, ty);
						this.repaint();
					}
				}
			}
		}
		
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			timer.draw(g);
			minesleft.draw(g);
			for(int i = 0; i < tWidth; i++)
			{
				for(int j = 0; j < tHeight; j++)
				{
					tiles[i][j].draw(g);
				}
			}
		}

		@Override public void mousePressed(MouseEvent e)
		{
			click(e);
		}
		
		@Override public void mouseClicked(MouseEvent e){}
		@Override public void mouseReleased(MouseEvent e){}
		@Override public void mouseExited(MouseEvent e){}
		@Override public void mouseEntered(MouseEvent e){}
	}
	
	/*public static class MainMenu extends JPanel
	{
		private JButton play, settings, exit;
		private JLabel title;
	}*/
	
	public static void main(String[] args)
	{
		JFrame window = new JFrame("MineSweeper, Jozwald Style!");
		Game g = new Game();
		window.add(g);
		window.setSize(500,500);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	}
}