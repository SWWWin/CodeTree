import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.PriorityQueue;
import java.util.StringTokenizer;



public class Main {
    static char[][] map;
    static int[] dx = {-1, 1, 0, 0};
    static int[] dy = {0, 0, -1, 1};
    static int N;
    public static void main(String[] args) throws NumberFormatException, IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;
        N = Integer.parseInt(br.readLine());
        
        map = new char[N + 1][N + 1];
        
        for(int row = 1; row <= N; row ++) {
            String str = br.readLine();
            for(int col = 1; col <= N; col ++) {
                map[row][col] = str.charAt(col - 1);
            }
        }
        
        int Q = Integer.parseInt(br.readLine());
        
        for(int q = 0; q < Q; q ++) {
            st = new StringTokenizer(br.readLine());
            
            int nowX = Integer.parseInt(st.nextToken());
            int nowY = Integer.parseInt(st.nextToken());
            int endX = Integer.parseInt(st.nextToken());
            int endY = Integer.parseInt(st.nextToken());
            int jump = 1;
            int time = 0;
            
            boolean[][][] visited = new boolean[N + 1][N + 1][6];
            
            PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[3] - b[3]);
            pq.offer(new int[] {nowX, nowY, jump, time});
            
            boolean isArrived = false;
            while(!pq.isEmpty()) {
                int[] arr = pq.poll();
                
                if (visited[arr[0]][arr[1]][arr[2]]) continue;

                visited[arr[0]][arr[1]][arr[2]] = true;
                
                if(arr[0] == endX && arr[1] == endY) {
                    isArrived = true;
                    System.out.println(arr[3]);
                    break;
                }
                

                if(arr[2] < 5) {
                    if(!visited[arr[0]][arr[1]][arr[2] + 1]) {
                        pq.offer(new int[] {arr[0], arr[1], arr[2] + 1, arr[3] + (arr[2] + 1) * (arr[2] + 1)});
                    
                    }
                }
                
                for(int j = 1; j < arr[2]; j ++) {
                    if(!visited[arr[0]][arr[1]][j]) {
                        pq.offer(new int[] {arr[0], arr[1], j, arr[3] + 1});
                        
                    }
                }
                
                for(int d = 0; d < 4; d ++) {
                    
                    if(isVaild(arr[0], arr[1], arr[2], d)) {
                        pq.offer(new int[] {arr[0] + dx[d] * arr[2], arr[1] + dy[d] * arr[2], arr[2], arr[3] + 1});
                        
                    }
                }
            }
            
            if(!isArrived) System.out.println(-1);
        }
        
        
    }
    

    static boolean isVaild(int x, int y, int jump, int dir) {

        for(int step = 1; step <= jump; step++) {
            int nx = x + dx[dir] * step;
            int ny = y + dy[dir] * step;

            if(nx <= 0 || nx > N || ny <= 0 || ny > N) {
                return false;
            }

            // 지나가는 도중 천적 돌이 있으면 점프 불가
            if(map[nx][ny] == '#') {
                return false;
            }
        }

        // 착지 가능한 돌인지 확인
        int nx = x + dx[dir] * jump;
        int ny = y + dy[dir] * jump;

        return map[nx][ny] == '.';
    }
}
