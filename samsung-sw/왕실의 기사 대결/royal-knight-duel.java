import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;
import java.util.StringTokenizer;

class Knight {
    int r;
    int c;
    int h;
    int w;
    int k;
    
    public Knight(int r, int c, int h, int w, int k) {
        super();
        this.r = r;
        this.c = c;
        this.h = h;
        this.w = w;
        this.k = k;
    }
    
}

public class Main {
    static int[][] map;
    static Knight[] knights;
    static int[] dr = {-1, 0, 1, 0};
    static int[] dc = {0, 1, 0, -1};
    static int L, N, Q;
    static boolean[] visited;
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        
        L = Integer.parseInt(st.nextToken());
        N = Integer.parseInt(st.nextToken());
        Q = Integer.parseInt(st.nextToken());
        
        map = new int[L+ 1][L + 1];
        
        for(int i = 1; i <= L; i ++) {
            st = new StringTokenizer(br.readLine());
            for(int j = 1; j <= L; j ++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        
        knights = new Knight[N + 1];
        int[] initialKnights = new int[N + 1];
        
        for(int n = 1; n <= N; n ++) {
            st = new StringTokenizer(br.readLine());
            int r = Integer.parseInt(st.nextToken());
            int c = Integer.parseInt(st.nextToken());
            int h = Integer.parseInt(st.nextToken());
            int w = Integer.parseInt(st.nextToken());
            int k = Integer.parseInt(st.nextToken());
            initialKnights[n] = k;
            knights[n] = new Knight(r, c, h, w, k);
        }
        
        
        for(int q = 0; q < Q; q ++) {
            st = new StringTokenizer(br.readLine());
            int k = Integer.parseInt(st.nextToken());
            int move = Integer.parseInt(st.nextToken());
            
            if(canMove(k, move)) {
                move(k, move);
            }
        }
        
        int answer = 0;
        for(int n = 1; n <= N; n ++) {
            if(knights[n] != null) {
                answer += initialKnights[n] - knights[n].k;
            }
        }
        
        System.out.println(answer);
    }

    private static boolean isValid(int r, int c, int h, int w) {
        for(int i = 0; i < h; i ++) {
            for(int j = 0; j < w; j ++) {
                if(r + i < 1 || r + i > L || c + j < 1 || c + j > L || map[r + i][c + j] == 2) return false;
            }
        }
        return true;
    }
    
    private static boolean canMove(int k, int move) {
        visited = new boolean[N + 1];
        
        if(knights[k] == null) return false;
        
        Queue<Integer> q = new ArrayDeque<>();
        q.offer(k);
        visited[k] = true;
        
        while(!q.isEmpty()) {
            int i = q.poll();
            
            int r = knights[i].r + dr[move];
            int c = knights[i].c + dc[move];
            
            if(!isValid(r, c, knights[i].h, knights[i].w)) return false; 
            
            for (int j = 1; j <= N; j++) {
                if (i == j || knights[j] == null || visited[j]) continue;

                Knight a = knights[i];
                Knight b = knights[j];

                boolean rowOverlap =
                    r <= b.r + b.h - 1 &&
                    b.r <= r + a.h - 1;

                boolean colOverlap =
                    c <= b.c + b.w - 1 &&
                    b.c <= c + a.w - 1;

                if (rowOverlap && colOverlap) {
                    visited[j] = true;
                    q.offer(j);
                }
            }
        }
        return true;
    }
    
    private static void move(int k, int move) {
        for(int i = 1; i <= N; i ++) {
            
            if(visited[i]) {
                knights[i].r += dr[move];
                knights[i].c += dc[move];
                
                if(k == i) continue;
                for(int h = 0; h < knights[i].h; h ++) {
                    for(int w = 0; w < knights[i].w; w ++) {
                        
                        if(map[h + knights[i].r][w + knights[i].c] == 1) {
                            knights[i].k --;
                        }
                        
                    }
                }
                
                if(knights[i].k <= 0) knights[i] = null;
            }
        }
    }

    private static void nowPrint(int k, int move) {
        System.out.println("map");
        for(int i = 0; i < map.length; i ++) {
            System.out.println(Arrays.toString(map[i]));
        }
        
        
        System.out.println("k: " + k + ", move: " + move);
    }
}
