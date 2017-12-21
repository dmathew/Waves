Waves
=====

Waves is a quick and dirty method of plotting moving graphs. Usage:
java -jar waves.jar input_file n_streams time_res stream_name ...

input_file
  The file to read data from. '-' means read from STDIN.
n_streams
  The number of moving line graphs to plot in the same area.
time_res
  Time in milliseconds to wait before plotting each point. For plotting real time data, keep this value low - waves will plot as soon as a new data point is received.
stream_name
  The name for each stream that will be displayed in the legend area.
