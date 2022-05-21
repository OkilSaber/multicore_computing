#include <omp.h>
#include <stdio.h>
#include <stdlib.h>

long num_steps = 10000000;
double step;

int main(int argc, char **argv)
{
    if (argc != 4)
    {
        printf("Wrong number of arguments.\n");
        return -1;
    }
    int mode = atoi(argv[1]);
    int chunk_size = atoi(argv[2]);
    int nb_threads = atoi(argv[3]);
    if (nb_threads <= 0)
        nb_threads = 1;
    if (chunk_size <= 0)
        chunk_size = -1;
    printf("Mode: %d with a chunk size of %d\nNumber of threads: %d\n", mode, chunk_size, nb_threads);
    omp_set_num_threads(nb_threads);
    switch (mode)
    {
    case 1:
        omp_set_schedule(omp_sched_static, chunk_size);
        break;
    case 2:
        omp_set_schedule(omp_sched_dynamic, chunk_size);
        break;
    case 3:
        omp_set_schedule(omp_sched_guided, chunk_size);
        break;
    default:
        printf("Invalid mode.\n");
        return (-1);
    }

    long i;
    double x, pi, sum = 0.0;
    double start_time, end_time;

    start_time = omp_get_wtime();
    step = 1.0 / (double)num_steps;
#pragma omp parallel for private(x) reduction(+ \
                                              : sum)
    for (i = 0; i < num_steps; i++)
    {
        x = (i + 0.5) * step;
        sum = sum + 4.0 / (1.0 + x * x);
    }
    pi = step * sum;
    end_time = omp_get_wtime();
    double timeDiff = end_time - start_time;
    printf("Execution Time : %.fms\n", timeDiff * 1000);

    printf("pi=%.24lf\n", pi);
}