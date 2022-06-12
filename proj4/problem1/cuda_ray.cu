#include <stdio.h>
#include <assert.h>
#include <cuda.h>
#include <cuda_runtime.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <time.h>
#include <math.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <time.h>
#include <math.h>

#define CUDA 0
#define OPENMP 1
#define SPHERES 20

#define rnd( x ) (x * rand() / RAND_MAX)
#define INF 2e10f
#define DIM 2048



typedef struct Sphere {
    float r, g, b;
    float radius;
    float x, y, z;
} Sphere;

__device__ float hit(Sphere* sphere, float ox, float oy, float *n)
{
    float dx = ox - sphere->x;
    float dy = oy - sphere->y;
    if (dx * dx + dy * dy < sphere->radius * sphere->radius) {
        float dz = sqrtf(sphere->radius * sphere->radius - dx * dx - dy * dy);
        *n = dz / sqrtf(sphere->radius * sphere->radius);
        return dz + sphere->z;
    } else 
        return -INF;
}

__global__ void kernel(int x, int y, Sphere* s, unsigned char* ptr)
{
    int offset = x + y*DIM;
    float ox = (x - DIM/2);
    float oy = (y - DIM/2);

    // printf("x:%d, y:%d, ox:%f, oy:%f\n",x,y,ox,oy);

    float r=0, g=0, b=0;
    float   maxz = -INF;
    for(int i=0; i<SPHERES; i++) {
        float n;
        float t = hit(&s[i], ox, oy, &n);
        if (t > maxz) {
            float fscale = n;
            r = s[i].r * fscale;
            g = s[i].g * fscale;
            b = s[i].b * fscale;
            maxz = t;
        }
    }

    ptr[offset*4 + 0] = (int)(r * 255);
    ptr[offset*4 + 1] = (int)(g * 255);
    ptr[offset*4 + 2] = (int)(b * 255);
    ptr[offset*4 + 3] = 255;
}

void ppm_write(unsigned char* bitmap, int xdim,int ydim, FILE* fp)
{
    int i,x,y;
    fprintf(fp,"P3\n");
    fprintf(fp,"%d %d\n",xdim, ydim);
    fprintf(fp,"255\n");
    for (y=0;y<ydim;y++) {
        for (x=0;x<xdim;x++) {
            i=x+y*xdim;
            fprintf(fp,"%d %d %d ",bitmap[4*i],bitmap[4*i+1],bitmap[4*i+2]);
        }
        fprintf(fp,"\n");
    }
}

int main(int argc, char* argv[])
{
    int x,y;
    unsigned char* bitmap;

    srand(time(NULL));

    if (argc != 1) {
        printf("Too many arguments\n");
        exit(0);
    }
    FILE* fp = fopen("result.ppm","w");
    
    Sphere *temp_s = (Sphere*)malloc( sizeof(Sphere) * SPHERES );
    for (int i=0; i<SPHERES; i++) {
        temp_s[i].r = rnd( 1.0f );
        temp_s[i].g = rnd( 1.0f );
        temp_s[i].b = rnd( 1.0f );
        temp_s[i].x = rnd( 2000.0f ) - 1000;
        temp_s[i].y = rnd( 2000.0f ) - 1000;
        temp_s[i].z = rnd( 2000.0f ) - 1000;
        temp_s[i].radius = rnd( 200.0f ) + 40;
    }
    
    bitmap=(unsigned char*)malloc(sizeof(unsigned char)*DIM*DIM*4);
    for (x=0;x<DIM;x++)
        for (y=0;y<DIM;y++)
            kernel<<<1, 1>>>(x,y,temp_s,bitmap);
    ppm_write(bitmap,DIM,DIM,fp);
    // printf("Openmp (%i threads): %fms\n", no_threads, total_time * 1000);
    // printf("[%s.ppm] generated\n", argv[2]);

    fclose(fp);
    free(bitmap);
    free(temp_s);

    return 0;
}