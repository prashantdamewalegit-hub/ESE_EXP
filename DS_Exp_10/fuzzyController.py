import numpy as np
import skfuzzy as fuzz
from skfuzzy import control as ctrl

# 1. Define input and output variables

density = ctrl.Antecedent(np.arange(0, 51, 1), 'density')
waiting = ctrl.Antecedent(np.arange(0, 121, 1), 'waiting')
green = ctrl.Consequent(np.arange(10, 91, 1), 'green')


# 2. Define membership functions

# Traffic density
density['low'] = fuzz.trapmf(density.universe, [0, 0, 10, 15])
density['medium'] = fuzz.trimf(density.universe, [10, 20, 30])
density['high'] = fuzz.trapmf(density.universe, [25, 35, 50, 50])

# Waiting time
waiting['short'] = fuzz.trapmf(waiting.universe, [0, 0, 20, 30])
waiting['moderate'] = fuzz.trimf(waiting.universe, [20, 50, 80])
waiting['long'] = fuzz.trapmf(waiting.universe, [70, 90, 120, 120])

# Green light duration
green['short'] = fuzz.trapmf(green.universe, [10, 10, 20, 30])
green['medium'] = fuzz.trimf(green.universe, [30, 45, 60])
green['long'] = fuzz.trapmf(green.universe, [60, 75, 90, 90])


# 3. Define fuzzy rules

r1 = ctrl.Rule(density['low'] & waiting['short'], green['short'])

r2 = ctrl.Rule(density['low'] & waiting['moderate'], green['medium'])

r3 = ctrl.Rule(density['low'] & waiting['long'], green['long'])

r4 = ctrl.Rule(density['medium'] & waiting['short'], green['medium'])

r5 = ctrl.Rule(density['medium'] & waiting['moderate'], green['medium'])

r6 = ctrl.Rule(density['medium'] & waiting['long'], green['long'])

r7 = ctrl.Rule(density['high'] & waiting['short'], green['medium'])

r8 = ctrl.Rule(density['high'] & waiting['moderate'], green['long'])

r9 = ctrl.Rule(density['high'] & waiting['long'], green['long'])


# 4. Create control system

traffic_ctrl = ctrl.ControlSystem([
    r1, r2, r3, r4, r5, r6, r7, r8, r9
])

simulation = ctrl.ControlSystemSimulation(traffic_ctrl)


# 5. Give input values

simulation.input['density'] = 10
simulation.input['waiting'] = 20


# 6. Calculate output

simulation.compute()


# 7. Display result

duration = simulation.output['green']

print("Traffic Density:", 10, "vehicles/min")
print("Waiting Time:", 20, "seconds")
print("Recommended Green Duration:", round(duration, 2), "seconds")