# Minesweeper-2016
A small Java implementation of minesweeper I made while just learning the language back in 2016.

## Background
I was sitting in AP Java playing minesweeper when my teacher came up behind me and said instead of playing minesweeper I should program it.
I obliged and spent the weekend making this little gem. Looking back 5 years later its fun to see how far I've come
```java 
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
```
Hard to belive I wrote this code unironically now. 

## Screenshot
As you can see, I wasn't very interested in the art aspect of this project. 
![Pic](https://github.com/James-Oswald/Minesweeper-2016/blob/master/screenshots/minesweeper.png)
