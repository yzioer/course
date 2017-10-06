# Exercise 3 - Money

This week's exercise is about the role of money and money supply. In contrast to previous exercises, each team will be experimenting on its own. There is no utility competition this week.

The relevant configuration of this exercise is named [MoneyConfiguration](../src/com.agentecon.exercise3.MoneyConfiguration.java) and is based on the configuration of the previous exercise. This time, you can take the agents as given, and take the role of a central bank instead. You can add [HelicopterMoneyEvents](../src/com.agentecon.exercise3.HelicopterMoneyEvent.java) and the [InterestEvents](../src/com.agentecon.exercise3.HelicopterMoneyEvent.java) to the configuration to run some monetary experiments. The helicopter event gives a lump sum of freshly printed money to one specific agent. The interest event pays some interest to every agent in the simulation. The interest is also paid from freshly printed money.

To solve this exercise, you will have to run the SimulationServer class locally, so that you can download relevant statistics through the web interface as shown in the lecture. Avoid using too extreme values as you play with this configuration. For example, the simulation could become unstable or even crash when printing extreme amounts of money.

## Task 1: Velocity

We have seen the Fisher equation in the lecture:

$MV = PT$ 

It says that money supply multiplied with its velocity should equal prices multiplied with transaction volume. Dividends are ignore.

Note that the velocity of money cannot be directly measured. Instead, it is calculated by measuring the other parameters first. Do so using the "monetary" statistics available in the web interface. In practice, economists often assume V to be a constant. Is this a reasonable assumption? Does this assumption hold in simulation?

The farmer and the farm both have a constant CAPITAL_BUFFER, that specifies how much income and capital they should put aside as a buffer to smoothen random fluctuations. How are the four variables in the Fisher equation affected when adjusting the size of that buffer?

## Task 2: Interest Rates

Now, we start paying interest on cash holdings. What effect does the printing of money (i.e. increasing M) have on the other three variables from the Fisher equation?

You probably know that central banks increase interest rates to fight inflation, and decrease interest rates to encourage inflation. Is this in line with what you observe in the simulation? If not, why do you think that is the case?

## Task 4: Lump sum subsidies

In classic theory, money is neutral in the long run. If there is twice as much money, prices will be twice as high, but everything else should stay the same. However, what if we do not distribute the freshly printed money evenly? Can an agent gain an advantage by getting money while others do not? To try this out, use the helicopter event to give a particular agent some additional money every day and try to test whether that agents enjoys a higher utility than the others as a result.

Document your findings in the [lab journal](exercise03-journal.md).

The deadline for submitting your farmer and the lab journal to github is 2017-10-12 at 24:00.
