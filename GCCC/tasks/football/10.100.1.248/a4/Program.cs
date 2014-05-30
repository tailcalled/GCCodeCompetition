using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Football
{
    class Program
    {
        const int HOME = 0;
        const int ROAD = 1;

        static void Main(string[] args)
        {
            int playerCount = Int32.Parse(Console.ReadLine());
            int[][] player = new int[playerCount][];
            int[][] game = new int[playerCount][];

            for(int i = 0; i < playerCount; i++)
            {
                player[i] = new int[2];
                game[i] = new int[2];
                string s = Console.ReadLine();
                string[] sa = s.Split(' ');
                player[i][HOME] = Int32.Parse(sa[HOME]);
                player[i][ROAD] = Int32.Parse(sa[ROAD]);
            }

            for (int p = 0; p < playerCount; p++ )
            {
                for (int p2 = p + 1; p2 < playerCount; p2++ )
                {
                    game[p][HOME] += 1;
                    game[p2][HOME] += 1;
                    if (player[p2][HOME] == player[p][ROAD])
                    {
                        game[p][HOME] += 1;
                    }
                    else
                    {
                        game[p][ROAD] += 1;
                    }

                    if (player[p][HOME] == player[p2][ROAD])
                    {
                        game[p2][HOME] += 1;
                    }
                    else
                    {
                        game[p2][ROAD] += 1;
                    }
                }
            }

            for (int p = 0; p < playerCount; p++)
            {
                Console.WriteLine(game[p][HOME].ToString() + " " + game[p][ROAD].ToString());
            }
        }
    }
}
