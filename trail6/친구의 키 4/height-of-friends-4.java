import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(
            new InputStreamReader(System.in)
        );

        StringTokenizer st = new StringTokenizer(br.readLine());

        int N = Integer.parseInt(st.nextToken());
        int M = Integer.parseInt(st.nextToken());

        // 입력 순서를 보존
        int[][] edges = new int[M][2];

        for (int m = 0; m < M; m++) {
            st = new StringTokenizer(br.readLine());
            edges[m][0] = Integer.parseInt(st.nextToken());
            edges[m][1] = Integer.parseInt(st.nextToken());
        }

        List<Integer>[] graph = new ArrayList[N + 1];

        for (int n = 1; n <= N; n++) {
            graph[n] = new ArrayList<>();
        }

        int left = 1;
        int right = M;
        int answer = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            int[] indegree = new int[N + 1];

            // 이전 검사에서 사용한 간선 제거
            for (int n = 1; n <= N; n++) {
                graph[n].clear();
            }

            // 처음 mid개의 단서만 그래프에 추가
            for (int m = 0; m < mid; m++) {
                int a = edges[m][0];
                int b = edges[m][1];

                graph[a].add(b);
                indegree[b]++;
            }

            Queue<Integer> q = new ArrayDeque<>();

            for (int n = 1; n <= N; n++) {
                if (indegree[n] == 0) {
                    q.offer(n);
                }
            }

            int count = 0;

            // BFS 위상 정렬
            while (!q.isEmpty()) {
                int now = q.poll();
                count++;

                for (int next : graph[now]) {
                    indegree[next]--;

                    if (indegree[next] == 0) {
                        q.offer(next);
                    }
                }
            }

            if (count < N) {
                // 처음 mid개 단서 안에 모순이 존재
                answer = mid;
                right = mid - 1;
            } else {
                // 아직 모순이 없으므로 뒤쪽 탐색
                left = mid + 1;
            }
        }

        System.out.println(answer == -1 ? "Consistent" : answer);
    }
}