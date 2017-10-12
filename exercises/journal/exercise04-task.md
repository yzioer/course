# Exercise 4 - Growth

This week's exercise is about growth and death. It is structured in three parts, one part that can be solved by running the simulation locally, one part that can be solved with pen and paper, and a competitive part that requires you to upload your agent to be run in the common simulation.

## Task 0 - Growth

We extend the existing simulation from the previous exercise by continuously adding additional agents. Unlike the initial agents, these additional agents do not get a land endowment, so the amount of land stays constant as the population grows.

Run the [GrowthConfiguration](../src/com/agentecon/exercise4/GrowthConfiguration.java). You will find that firms start making profits after a while! Why do you think is that? Also, what is your interpretation of the diverging prices for man-hours and potatoes? Which agents benefit from the growing population, which agents do not?

Sidenote: if you study the growth configuration in detail, you will notice that money is being printed to ensure money supply grows with the economy, thereby keeping prices in a useful range. This effect is only nominal and not relevant for solving this task.

## Task 1: Savings Rule

When modeling growth through the birth of new agents, one consequently should also model death. Thus, we will make our agents mortal with a life-span of exactly 500 days. Overall, the population will still grow as long as the birth rate exceeds the death rate of 1/500 = 0.2%.

Furthermore, we add retirements to the model and disallow the agents to work as soon as they turn 400 days old. So if they still want to enjoy some utility during retirement, they must put aside some savings while working, and spend these savings in retirement.

This task can be solved entirely with pen and paper and good reasoning. Assuming that the agents maximize total life-time utility, how much should they save every day while working? And how much should of their savings should they spend in retirement?

## Task 2: Simulation

To test your savings heuristic, you should implement the two methods calculatecalculateDailyRetirementSpendings(double savings, int age)

## Deliverables and deadline

Document your findings in the [lab journal](exercise03-journal.md), maybe with the help of some nice charts where appropriate. You do not need to hand in any source code.

The deadline for submitting the lab journal to github is 2017-10-12 at 24:00.
