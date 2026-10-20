import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Main {
    static int max, N;
    static int[][] grid, memo;
    static boolean[][] visited;
    static int[] dr = {1, -1, 0, 0};
    static int[] dc = {0, 0, 1, -1};
    public static void main(String[] args) throws NumberFormatException, IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        N = Integer.parseInt(br.readLine());
        
        grid = new int[N][N];
        memo = new int[N][N];
        visited = new boolean[N][N];
        
        for (int i = 0; i < N; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                grid[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        
        max = 0;
        
        for(int n1 = 0; n1 < N; n1 ++) {
            for(int n2 = 0; n2 < N; n2 ++) {
                dfs(n1, n2);
            }
        }
        
        for(int n1 = 0; n1 < N; n1 ++) {
            for(int n2 = 0; n2 < N; n2 ++) {
                max = Math.max(max, memo[n1][n2]);
            }
        }
        
        
        System.out.println(max);
       
    }
    
    private static int dfs(int n1, int n2) {
        if(memo[n1][n2] != 0) {
            return memo[n1][n2];
        }
        
        memo[n1][n2] = 1;
        
        
        
        for(int i = 0; i < 4; i ++) {
            int nr = n1 + dr[i];
            int nc = n2 + dc[i];
            
            if(isVaild(nr,nc) && grid[n1][n2] < grid[nr][nc]) {
                int next = dfs(nr, nc);
                memo[n1][n2] = Math.max(memo[n1][n2], next + 1);
            }
        }
        
        return memo[n1][n2];
    }

    private static boolean isVaild(int nr, int nc) {
        if(nr >= 0 && nr < N && nc >= 0 && nc < N) {
            return true;
        }
        return false;
    }
    

}
