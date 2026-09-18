import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.StringTokenizer;

class Loc {
    int col;
    int row;

    public Loc(int col, int row) {
        super();
        this.col = col;
        this.row = row;
    }
}

public class Main {
    static int[] dc = {-1, 0, 0, 1};
    static int[] dr = {0, -1, 1, 0};
    static int N;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        N = Integer.parseInt(st.nextToken());
        int M = Integer.parseInt(st.nextToken());
        int[][] map = new int[N][N];
        ArrayList<Loc> baseCamp = new ArrayList<>();
        Loc[] conv = new Loc[M + 1];
        Loc[] playerLocation = new Loc[M + 1];
        boolean[] arrived = new boolean[M + 1];
        boolean[][] blocked = new boolean[N][N];
        
        for (int c = 0; c < N; c++) {
            st = new StringTokenizer(br.readLine());
            for (int r = 0; r < N; r++) {
                map[c][r] = Integer.parseInt(st.nextToken());

                if (map[c][r] == 1) {
                    Loc loc = new Loc(c, r);
                    baseCamp.add(loc);
                }
            }
        }

        for (int m = 1; m <= M; m++) {
            st = new StringTokenizer(br.readLine());

            int col = Integer.parseInt(st.nextToken()) - 1;
            int row = Integer.parseInt(st.nextToken()) - 1;

            map[col][row] = m;

            Loc loc = new Loc(col, row);
            conv[m] = loc;
        }

        int target = 1;
        int arrivedCount = 0;
        
        while (true) {
            

            for (int n = 1; n < target && n <= M; n++) {

                if (arrived[n])
                    continue;

                playerLocation[n]
                    = movePlayer(conv[n], playerLocation[n], blocked);
            }


            // 2. 이동 후 편의점에 도착했는지 확인
            for (int n = 1; n < target && n <= M; n++) {

                if (arrived[n])
                    continue;

                if (conv[n].col == playerLocation[n].col
                        && conv[n].row == playerLocation[n].row) {

                    arrived[n] = true;
                    arrivedCount ++;
                    
                    blocked[conv[n].col][conv[n].row] = true;
                }
            }


            // 3. target번 사람이 베이스캠프에 들어감
            if (target <= M) {

                Loc[] baseArray = baseCamp.toArray(new Loc[0]);

                Loc loc = search(conv[target], baseArray, blocked);

                playerLocation[target] = loc;
                
                blocked[loc.col][loc.row] = true;
            }


            if(arrivedCount == M) break;
            target++;
        }

        System.out.println(target);

    }

    public static Loc search(Loc conv, Loc[] baseCamp, boolean[][] blocked) {

        boolean[][] visited = new boolean[N][N];
        int[][] dist = new int[N][N];

        Queue<Loc> q = new ArrayDeque<>();

        q.add(conv);
        visited[conv.col][conv.row] = true;

        while (!q.isEmpty()) {

            Loc cur = q.poll();

            for (int dir = 0; dir < 4; dir++) {

                int nc = cur.col + dc[dir];
                int nr = cur.row + dr[dir];

                if (nc >= 0 && nc < N &&
                    nr >= 0 && nr < N &&
                    !visited[nc][nr] &&
                    !blocked[nc][nr]) {

                    visited[nc][nr] = true;
                    dist[nc][nr] =
                        dist[cur.col][cur.row] + 1;

                    q.add(new Loc(nc, nr));
                }
            }
        }

        int min = Integer.MAX_VALUE;
        Loc answer = null;

        for (int i = 0; i < baseCamp.length; i++) {

            Loc base = baseCamp[i];

            if (blocked[base.col][base.row])
                continue;


            if (!visited[base.col][base.row])
                continue;

            if (min > dist[base.col][base.row]) {
                min = dist[base.col][base.row];
                answer = base;
            }
        }

        return answer;
    }

    public static Loc movePlayer(Loc conv, Loc playerLocation, boolean[][] blocked) {
        boolean[][] visited = new boolean[N][N];
        int[][] dist = new int[N][N];
        Loc loc = new Loc(0, 0);
        Queue<Loc> q = new ArrayDeque<>();
        q.add(conv);
        visited[conv.col][conv.row] = true;
        
        while(!q.isEmpty()) {
            Loc l = q.poll();
            for (int dir = 0; dir < 4; dir++) {

                int nc = l.col + dc[dir];
                int nr = l.row + dr[dir];
                
                if(nc >= 0 && nc < N && nr >= 0 && nr < N && !visited[nc][nr] && !blocked[nc][nr]) {
                    visited[nc][nr] = true;
                    dist[nc][nr] = dist[l.col][l.row] + 1;
                    
                    q.add(new Loc(nc, nr));

                }
            }
        }

        int min = Integer.MAX_VALUE;

        for (int dir = 0; dir < 4; dir++) {
            int nc = playerLocation.col + dc[dir];
            int nr = playerLocation.row + dr[dir];

            if (nc >= 0 && nc < N && nr >= 0 && nr < N
                    && visited[nc][nr]
                    && !blocked[nc][nr]) {

                if (min > dist[nc][nr]) {
                    min = dist[nc][nr];
                    loc = new Loc(nc, nr);
                }
            }
        }

        return loc;
    }

    public static boolean isExist(Loc[] conv) {
        for (Loc con : conv) {
            if (con != null)
                return true;
        }

        return false;
    }
}
