package dev.meteorpurple.ui;

/** One immutable set of merged pixel spans per radius; shared by every panel. */
public final class RoundedGeometry {
    private static final int[][] SPANS=new int[97][];
    static {
        for(int r=0;r<SPANS.length;r++) {
            int[] work=new int[r*3]; int count=0;
            for(int row=0;row<r;) {
                int inset=inset(r,row),end=row+1;
                while(end<r && inset(r,end)==inset) end++;
                work[count++]=row; work[count++]=end; work[count++]=inset; row=end;
            }
            SPANS[r]=java.util.Arrays.copyOf(work,count);
        }
    }
    private RoundedGeometry() {}
    private static int inset(int radius,int row) {
        return (int)Math.ceil(radius-Math.sqrt(radius*radius-Math.pow(radius-row-.5,2)));
    }
    public static int[] spans(int radius) { return SPANS[Math.max(0,Math.min(96,radius))]; }
}
