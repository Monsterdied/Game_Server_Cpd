import numpy as np
import matplotlib.pyplot as plt

# Data
python_cols = [600, 1000, 1400, 1800, 2200, 2600, 3000]
python_times = [37.528, 129.709, 360.524, 801.004, 1447.02, 2473.374, 3882.680]
pythom_times_line = [34.32, 129.16, 363.34, 799.82, 1448.02, 2477.64, 3878.38]
c_plusplus_cols = [600, 1000, 1400, 1800, 2200, 2600, 3000]
c_plusplus_times = [0.361, 3.285, 7.882, 39.849, 83.774, 149.357, 251.849]
c_plusplus_times_line = [0.139, 0.683, 1.888, 4.026, 7.510, 12.426, 19.202]
c_plusplus_misses_l1_normal = [244732304, 1295249061, 3584361972, 9493799461, 17904217691, 31231163276, 50950647315]
c_plusplus_misses_l2_normal = [40217045, 203004872, 823283266, 15173292959, 26532938297, 57426186873, 111976069429]
c_plusplus_misses_l1_line = [27155344, 126230588, 347877309, 751178292, 2083477563, 4412550006, 6781861408]
c_plusplus_misses_l2_line = [56698562, 254570628, 687501672, 1473043616, 2670495829, 4406100883, 6763853500]


# Create a new figure
plt.figure()


plt.plot(c_plusplus_cols, c_plusplus_misses_l1_normal, label='C++ Misses L1 Normal')
plt.plot(c_plusplus_cols, c_plusplus_misses_l2_normal, label='C++ Misses L2 Normal')
plt.plot(c_plusplus_cols, c_plusplus_misses_l1_line, label='C++ Misses L1 Line')
plt.plot(c_plusplus_cols, c_plusplus_misses_l2_line, label='C++ Misses L2 Line')


# Add labels and title
plt.xlabel('Matrix Size (n x n)')
plt.ylabel('Cache Misses')
plt.title('C++ Cache Misses')
plt.grid(True)

# Add a legend
plt.legend()

# Save the plot to a file instead of showing it
plt.savefig('graph.png')