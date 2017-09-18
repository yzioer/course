# Exercise 1 - The Hermit

![hermit](images/hermit.jpg "A hermit's house")

A hermit is an autarkic consumer that does not interact with other agents. The hermit has a production function that turns man-hours into food. Your task is to program your own hermit with an optimal work-life balance, as the hermit enjoys both, food and man-hours spent as leisure time. The utility function of the hermit looks as follows (I recommend to install the plugin [Tex all the thing](https://chrome.google.com/webstore/detail/tex-all-the-things/cbimabofgmfdkicghcadidpemeenbffn) to display tex equations in the browser):

$U(h_{leisure}, x_{potatoes}) = log(h_{leisure}) + log(x_{potatoes})$

Thus, the hermit enjoys eating potatoes and spending man-hours as leisure time equally. In order to maximize utility, he needs to decide how much of his 24 hours to spend on leisure time and how much on growing potatoes according to the following budget constraint:

$h_{leisure} + h_{work} = 24$

The hours spent working are turned into potatoes via a Cobb-Douglas production function with fixed costs, with $x_{land}=100$ being constant:

$x_{potatoes}(x_{land}, x_{work}) = x_{work}^{0.6} x_{land}^{0.2}$

Plugging the production function and the budget constraint into the utility function, this leads to the following simplified maximization problem:

$max\ U(h_{work}) = log(24 - h_{work}) + log(x_{work}^{0.6} x_{land}^{0.2})$

If you want, you can solve this problem mathematically. Alternatively, you can also just play around with your [Hermit class](../src/com/agentecon/exercise1/Hermit.java) to find out how to achieve a better utility. To do so, adjust the function 'produce', in which the hermit decides how much time to spend on growing food.

It is possible to edit this class directly here on github.com (make sure the current url matches your team and you are logged in) and then check the [resulting ranking online](http://meissereconomics.com/vis/simulation?sim=ex1-hermit). However, in practice, it is much more efficient to edit and test a local copy of your agents before uploading (pushing) them to github, a process which is described [here](http://meissereconomics.com/course/setup).

Document your findings in the [lab journal](exercise01-journal.md) as you try out different ideas to make your hermit behave optimally.

The deadline for submitting your hermit and the lab journal to github is 2017-09-21 at 24:00.
