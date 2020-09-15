#define LS 16
#define M(v,n,i,j) (v)[(i)*(n)+(j)]

__kernel void matrix_mul(__global const int *A,
                         __global const int *B,
                         __global int *C,
                         int n,
                         int x) {
    
    // Get the index of the current element to be processed
    int i = get_global_id(0);
    int j = get_global_id(1);
    
    
    if (i>=n || j>=n) return;
    
    // Get local index of the current element to be processed
    int il = get_local_id(0);
    int jl = get_local_id(1);
    
    // Local memory
    __local int Al[LS][LS]; // = { 0 };
    __local int Bl[LS][LS]; // = { 0 };
    
    // Do the operation
    int k;
    int sum=0;
    for (k=0; k<x; k+=LS) {
        
        Al[il][jl] = A[i*x+k+jl]; 		// M(A,n,i,k+jl)
        Bl[il][jl] = B[k*n+il*n+j];		// M(B,n,k+il,j)
        
        barrier(CLK_LOCAL_MEM_FENCE);
        
        int kl;
        for (kl=0; kl<LS; kl++) sum += Al[il][kl]*Bl[kl][jl];
        
        barrier(CLK_LOCAL_MEM_FENCE);
    }
    C[i*n+j] = sum;						// M(C,n,i,j)
    
}
