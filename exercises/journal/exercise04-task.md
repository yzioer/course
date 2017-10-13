# Exercise 4 - Growth

This week's exercise is about growth, savings, and death. The first part can be solved with pen and paper, and the second part is about verifying the solution in the simulation.

## Task 1: Savings Rule

When modeling growth through the birth of new agents, one consequently should also model death. Thus, we will make our agents mortal with a life-span of exactly 500 days. Overall, the population will still grow as long as the birth rate exceeds the death rate of 1/500 = 0.2%.

Furthermore, we add retirements to the model and disallow the agents to work as soon as they turn 400 days old. So if they still want to enjoy some utility during retirement, they must put aside some savings while working, and spend these savings in retirement.

This task can be solved entirely with pen and paper and good reasoning. Assuming that the agents maximize total life-time utility, how much should they save every day while working? And how much should of their savings should they spend in retirement?

## Task 2: Simulation

To test your savings heuristic, you should implement the two methods calculatecalculateDailyRetirementSpendings(double savings, int age)

## Deliverables and deadline

Document your findings in the [lab journal](exercise03-journal.md), maybe with the help of some nice charts where appropriate. You do not need to hand in any source code.

The deadline for submitting the lab journal to github is 2017-10-12 at 24:00.
