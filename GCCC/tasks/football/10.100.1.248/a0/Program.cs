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
            foreach(int[] playerColour in player)
            {
                string s = Console.ReadLine();
                string[] sa = s.Split(' ');
                playerColour[HOME] = Int32.Parse(sa[HOME]);
                playerColour[ROAD] = Int32.Parse(sa[ROAD]);
            }

            int[][] game = new int[playerCount][];

            for (int p = 0; p < player.Length/2; p++ )
            {
                for (int p2 = 0; p2 < player.Length / 2; p2++ )
                {
                    if (p == p2) continue;

                    game[p][HOME] += 1;
                    if (player[p2][HOME] == player[p][ROAD])
                        game[p][HOME] += 1;
                    else
                        game[p][ROAD] += 1;
                }
            }

            for (int p = 0; p < player.Length / 2; p++)
            {
                Console.WriteLine(game[p][HOME].ToString() + " " + game[p][ROAD].ToString());
            }
        }
    }
}
