import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Main {
    static final int LENGTH = 5;
    static final int TURN_SIZE = 3;

    static int[][] map = new int[LENGTH][LENGTH];
    static int[][] parentR = new int[LENGTH][LENGTH];
    static int[][] parentC = new int[LENGTH][LENGTH];
    static int[][] size = new int[LENGTH][LENGTH];
    static int[] walls;

    static int[] dr = {-1, 1, 0, 0};
    static int[] dc = {0, 0, -1, 1};

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        int K = Integer.parseInt(st.nextToken());
        int M = Integer.parseInt(st.nextToken());

        for (int r = 0; r < LENGTH; r++) {
            st = new StringTokenizer(br.readLine());
            for (int c = 0; c < LENGTH; c++) {
                map[r][c] = Integer.parseInt(st.nextToken());
            }
        }

        walls = new int[M];
        st = new StringTokenizer(br.readLine());
        for (int i = 0; i < M; i++) {
            walls[i] = Integer.parseInt(st.nextToken());
        }

        int step = 0;
        StringBuilder answer = new StringBuilder();

        for (int k = 0; k < K; k++) {
            int bestScore = 0;
            int bestDir = 0;
            int bestR = 0;
            int bestC = 0;

            // 각도 → 열 → 행 순으로 보면 점수가 같을 때 먼저 본 후보가 우선이다.
            for (int dir = 1; dir <= 3; dir++) {
                for (int c = 1; c < LENGTH - 1; c++) {
                    for (int r = 1; r < LENGTH - 1; r++) {
                        rotate(dir, r, c);
                        int score = addArtifact(false);

                        // 점수만 확인하고 원래 격자로 돌려놓는다.
                        rotate(4 - dir, r, c);

                        if (score > bestScore) {
                            bestScore = score;
                            bestDir = dir;
                            bestR = r;
                            bestC = c;
                        }
                    }
                }
            }

            // 어느 쪽으로 돌려도 얻는 게 없으면 탐사를 끝낸다.
            if (bestScore == 0) {
                break;
            }

            rotate(bestDir, bestR, bestC);

            int turnScore = 0;
            int gained;

            // 얻은 유물을 없애고 빈칸을 채운 다음, 또 얻을 수 있는지 확인한다.
            while ((gained = addArtifact(true)) > 0) {
                turnScore += gained;
                step = fill(step);
            }

            if (answer.length() > 0) {
                answer.append(' ');
            }
            answer.append(turnScore);
        }

        System.out.println(answer);
    }

    private static int addArtifact(boolean remove) {
        init();

        // 같은 숫자가 상하좌우로 붙어 있으면 같은 그룹으로 묶는다.
        for (int r = 0; r < LENGTH; r++) {
            for (int c = 0; c < LENGTH; c++) {
                for (int d = 0; d < 4; d++) {
                    int nr = r + dr[d];
                    int nc = c + dc[d];

                    if (nr < 0 || nr >= LENGTH || nc < 0 || nc >= LENGTH) {
                        continue;
                    }

                    if (map[r][c] == map[nr][nc]) {
                        union(r, c, nr, nc);
                    }
                }
            }
        }

        int score = 0;

        // 그룹을 다 묶고 나서, 크기가 3 이상인 그룹에 속한 칸을 하나씩 센다.
        for (int r = 0; r < LENGTH; r++) {
            for (int c = 0; c < LENGTH; c++) {
                int[] root = find(r, c);

                if (size[root[0]][root[1]] >= 3) {
                    score++;

                    // 실제로 유물을 얻는 단계라면 이 칸을 비운다.
                    if (remove) {
                        map[r][c] = 0;
                    }
                }
            }
        }

        return score;
    }

    private static void init() {
        for (int r = 0; r < LENGTH; r++) {
            for (int c = 0; c < LENGTH; c++) {
                parentR[r][c] = r;
                parentC[r][c] = c;
                size[r][c] = 1;
            }
        }
    }

    private static int[] find(int r, int c) {
        if (parentR[r][c] == r && parentC[r][c] == c) {
            return new int[]{r, c};
        }

        int[] root = find(parentR[r][c], parentC[r][c]);
        parentR[r][c] = root[0];
        parentC[r][c] = root[1];

        return root;
    }

    private static void union(int r1, int c1, int r2, int c2) {
        int[] rootA = find(r1, c1);
        int[] rootB = find(r2, c2);

        if (rootA[0] == rootB[0] && rootA[1] == rootB[1]) {
            return;
        }

        parentR[rootB[0]][rootB[1]] = rootA[0];
        parentC[rootB[0]][rootB[1]] = rootA[1];
        size[rootA[0]][rootA[1]] += size[rootB[0]][rootB[1]];
    }

    private static void rotate(int dir, int r, int c) {
        int top = r - 1;
        int left = c - 1;
        int[][] rotated = new int[TURN_SIZE][TURN_SIZE];

        // 시계 방향 90도 회전을 dir번 반복한다.
        for (int turn = 0; turn < dir; turn++) {
            for (int row = 0; row < TURN_SIZE; row++) {
                for (int col = 0; col < TURN_SIZE; col++) {
                    rotated[col][TURN_SIZE - 1 - row] = map[top + row][left + col];
                }
            }

            for (int row = 0; row < TURN_SIZE; row++) {
                for (int col = 0; col < TURN_SIZE; col++) {
                    map[top + row][left + col] = rotated[row][col];
                }
            }
        }
    }

    private static int fill(int step) {
        // 왼쪽 열부터 보고, 같은 열 안에서는 아래에서 위로 채운다.
        for (int c = 0; c < LENGTH; c++) {
            for (int r = LENGTH - 1; r >= 0; r--) {
                if (map[r][c] == 0) {
                    map[r][c] = walls[step++];
                }
            }
        }

        return step;
    }
}