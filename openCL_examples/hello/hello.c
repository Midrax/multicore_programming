#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#ifdef __APPLE__
#include <OpenCL/opencl.h>
#else
#include <CL/cl.h>
#endif

#define MAX_STR_SIZE 16

/* program written in OpenCL C made of a single kernel function to be executed on the GPU */
const char* kernel_source[] = {
	"__kernel void hello(__global char* string) {"
	"    int i;"
	"    char s[] = \"Hello world\";"
	"    for (i = 0; i < sizeof(s); i++) string[i] = s[i];"
	"}"
};

int main() {
    
	cl_int ret;
    
	/* 1) Context object */
	cl_platform_id platform_id = NULL;
	cl_device_id device_id = NULL;
	cl_context context = NULL;
	
	/* 2) Command queue */
	cl_command_queue command_queue = NULL;
	
	/* 3) Memory object */
	cl_mem memobj = NULL;
	
	/* 4) Program object */
	cl_program program = NULL;
    
	/* 5) Kernel object */
	cl_kernel kernel = NULL;
    
	/* Get available GPU device info */
	ret = clGetPlatformIDs(1, &platform_id, NULL);
	ret = clGetDeviceIDs(platform_id, CL_DEVICE_TYPE_GPU, 1, &device_id, NULL);
    
	/* Create OpenCL context */
	context = clCreateContext(NULL, 1, &device_id, NULL, NULL, &ret);
    
	/* Create memory buffer */
	memobj = clCreateBuffer(context, CL_MEM_READ_WRITE, MAX_STR_SIZE*sizeof(char), NULL, &ret);
	
	/* Create kernel program from the source */
	program = clCreateProgramWithSource(context, 1, kernel_source, NULL, &ret);
	
	/* Build kernel program */
	ret = clBuildProgram(program, 1, &device_id, NULL, NULL, NULL);
	
	/* Create OpenCL kernel */
	kernel = clCreateKernel(program, "hello", &ret);
	
	/* Set OpenCL kernel parameters */
	ret = clSetKernelArg(kernel, 0, sizeof(cl_mem), (void *)&memobj);
    
	/* Create Command Queue */
	command_queue = clCreateCommandQueue(context, device_id, 0, &ret);
    
	/* Execute OpenCL kernel */
	ret = clEnqueueTask(command_queue, kernel, 0, NULL, NULL);
	
	/* Copy results from device to host */
	char string2[MAX_STR_SIZE];
	ret = clEnqueueReadBuffer(command_queue, memobj, CL_TRUE, 0,
	                          MAX_STR_SIZE * sizeof(char), string2, 0, NULL, NULL);
	
	/* Display result */
	puts(string2);
    
	/* Release resources */
	ret = clReleaseKernel(kernel);
	ret = clReleaseProgram(program);
	ret = clReleaseMemObject(memobj);
	ret = clReleaseCommandQueue(command_queue);
	ret = clReleaseContext(context);
    
	return 0;
}
