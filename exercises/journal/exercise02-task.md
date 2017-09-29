# Exercise 2 - The Famer

Instead of farming for itself in solitude as in exercise 1, your agent is now interacting with others through a common market. In fact, there will be multiple instances of your agent class in the simulation and not just one. The resulting score is the average of these instances.

The utility and production functions are still the same as the last time, but maybe we can do better collectively?

Note that the simulation is configured such that in the efficient equilibrium, the optimal number of firms is lower than number of consumers. In fact, it is easy to see that with seven hermits working on six farms, total output is slightly higher than before:

The hermit's optimal production: $(12.75-6)^{0.6} 100^{0.2}=7.899$

Seven hermits using the same amount of man-hours on six farms: \frac{6}{7} $(\frac{7}{6} 12.75-6)^{0.6} 100^{0.2} = 7.97911$

The question is, will the agents be able to coordinate on such an equilibrium? Is it even an equilibrium? Is it stable? It is not clear a priori whether this is the case, as our market is not complete. I.e. we cannot trade shares yet, and we cannot trade land yet.

If you want, you can solve this problem mathematically. Alternatively, you can also just play around with your [Farmer class](../src/com/agentecon/exercise2/Farmer.java) and the [Farm](../src/com/agentecon/exercise2/Farmer.java) he is creating to find out how to achieve a better utility. After having pushed your code to github, you can check out the [resulting ranking online](http://meissereconomics.com/vis/simulation?sim=ex2-farmer). This is already much harder than the previous exercise. You do not need to come up with a perfect solutions. You can already reach the full score with well-reasoned attempts and considerations.

Document your findings in the [lab journal](exercise02-journal.md) as you try out different ideas to make your agent behave well.

The deadline for submitting your hermit and the lab journal to github is 2017-10-05 at 24:00.
