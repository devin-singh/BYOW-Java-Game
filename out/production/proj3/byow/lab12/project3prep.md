# Project 3 Prep

**For tessellating hexagons, one of the hardest parts is figuring out where to place each hexagon/how to easily place hexagons on screen in an algorithmic way.
After looking at your own implementation, consider the implementation provided near the end of the lab.
How did your implementation differ from the given one? What lessons can be learned from it?**

Answer:

I should have made a Position class that can take in an offset and do the calculations for me within the position class.
I should have then made my functions that draw the Hexagon from a Position.

**Can you think of an analogy between the process of tessellating hexagons and randomly generating a world using rooms and hallways?
What is the hexagon and what is the tesselation on the Project 3 side?**

Answer:

The hexagons are being combined together randomly in this lab and in project three rooms and hallways are going to be combined randomly.

**If you were to start working on world generation, what kind of method would you think of writing first? 
Think back to the lab and the process used to eventually get to tessellating hexagons.**

Answer:

I would create a method to draw a single line from a Position and a direction and then construct a room from that. I would then continue to slightly increase complexity from there. Moving to a room then hallways and then create a way for rooms to be added and expanded to the current room.
**What distinguishes a hallway from a room? How are they similar?**

Answer:

Both contain rectangular spaces where the player can move through. A hallway differs in that it is of width 1 and it connects two rooms together.