import java.io.*;
import java.util.*;

public class Main {
    static class Edge {
        int from, to;
        long weight;
        int id;

        Edge(int from, int to, long weight, int id) {
            this.from = from;
            this.to = to;
            this.weight = weight;
            this.id = id;
        }
    }

    static int N, LOG;
    static int[] parent, depth, tin, tout, nodeAt;
    static int[][] up;
    static long[] dist;
    static ArrayList<Edge>[] tree;

    static int[] diff;
    static int allAdd;
    static int timer;

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        N = Integer.parseInt(st.nextToken());
        int M = Integer.parseInt(st.nextToken());

        Edge[] edges = new Edge[M];
        tree = new ArrayList[N + 1];

        for (int i = 1; i <= N; i++) {
            tree[i] = new ArrayList<>();
        }

        for (int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine());

            int from = Integer.parseInt(st.nextToken());
            int to = Integer.parseInt(st.nextToken());
            long weight = Long.parseLong(st.nextToken());

            edges[i] = new Edge(from, to, weight, i);
        }

        // 1. 크루스칼로 MST 만들기
        Arrays.sort(edges, Comparator.comparingLong(e -> e.weight));

        int[] parents = new int[N + 1];
        for (int i = 1; i <= N; i++) {
            parents[i] = i;
        }

        boolean[] used = new boolean[M];
        int count = 0;

        for (Edge edge : edges) {
            if (union(edge.from, edge.to, parents)) {
                used[edge.id] = true;

                tree[edge.from].add(new Edge(edge.from, edge.to, edge.weight, 0));
                tree[edge.to].add(new Edge(edge.to, edge.from, edge.weight, 0));

                count++;
                if (count == N - 1) break;
            }
        }

        // 2. MST를 루트 1 기준 트리로 만들기
        LOG = 1;
        while ((1 << LOG) <= N) LOG++;

        parent = new int[N + 1];
        depth = new int[N + 1];
        tin = new int[N + 1];
        tout = new int[N + 1];
        nodeAt = new int[N + 1];
        dist = new long[N + 1];
        up = new int[LOG][N + 1];

        makeTreeInfo();

        for (int k = 1; k < LOG; k++) {
            for (int v = 1; v <= N; v++) {
                up[k][v] = up[k - 1][up[k - 1][v]];
            }
        }

        // 3. MST에 포함되지 않은 간선으로 불가능한 시작점 제거
        diff = new int[N + 2];

        for (Edge edge : edges) {
            if (used[edge.id]) continue;

            long treeDistance = getDistance(edge.from, edge.to);
            long limit = treeDistance - edge.weight;

            // MST 경로 길이가 간선 가중치보다 짧거나 같으면 문제 없음
            if (limit <= 0) continue;

            removeBadSide(edge.from, edge.to, limit);
            removeBadSide(edge.to, edge.from, limit);
        }

        // 4. 남은 정점 출력
        boolean[] bad = new boolean[N + 1];
        int sum = 0;

        for (int i = 1; i <= N; i++) {
            sum += diff[i];
            int vertex = nodeAt[i];

            if (sum + allAdd > 0) {
                bad[vertex] = true;
            }
        }

        StringBuilder answer = new StringBuilder();

        for (int i = 1; i <= N; i++) {
            if (!bad[i]) {
                answer.append(i).append(" ");
            }
        }

        System.out.println(answer.length() == 0 ? -1 : answer.toString().trim());
    }

    static void makeTreeInfo() {
        ArrayDeque<int[]> stack = new ArrayDeque<>();

        stack.push(new int[] {1, 0, 0});

        while (!stack.isEmpty()) {
            int[] now = stack.pop();

            int current = now[0];
            int prev = now[1];
            int state = now[2];

            if (state == 0) {
                tin[current] = ++timer;
                nodeAt[timer] = current;

                parent[current] = prev;
                up[0][current] = prev;

                stack.push(new int[] {current, prev, 1});

                for (Edge next : tree[current]) {
                    if (next.to == prev) continue;

                    depth[next.to] = depth[current] + 1;
                    dist[next.to] = dist[current] + next.weight;

                    stack.push(new int[] {next.to, current, 0});
                }
            } else {
                tout[current] = timer;
            }
        }
    }

    // from 쪽에서 너무 가까운 시작점들을 제거
    static void removeBadSide(int from, int to, long limit) {
        int lca = lca(from, to);

        long fromToLca = dist[from] - dist[lca];

        int last;
        int next;

        // from -> lca 구간 안에서 경계가 생기는 경우
        if (2 * fromToLca >= limit) {
            last = climbStrict(from, lca, limit);
            next = parent[last];
        }
        // lca -> to 구간에서 경계가 생기는 경우
        else {
            long lcaToTo = dist[to] - dist[lca];
            long remain = limit - 2 * fromToLca;
            long 기준 = 2 * lcaToTo - remain;

            int child = climbWithin(to, lca, 기준);

            last = parent[child];
            next = child;
        }

        addComponent(last, next);
    }

    // current에서 ancestor 방향으로 이동:
    // current부터 이동한 거리 * 2 < limit 을 만족하는 가장 먼 조상
    static int climbStrict(int current, int ancestor, long limit) {
        int start = current;

        for (int k = LOG - 1; k >= 0; k--) {
            int next = up[k][current];

            if (next != 0
                    && depth[next] >= depth[ancestor]
                    && 2 * (dist[start] - dist[next]) < limit) {
                current = next;
            }
        }

        return current;
    }

    // current에서 ancestor 방향으로 이동:
    // current부터 이동한 거리 * 2 <= limit 을 만족하는 가장 먼 조상
    static int climbWithin(int current, int ancestor, long limit) {
        int start = current;

        for (int k = LOG - 1; k >= 0; k--) {
            int next = up[k][current];

            if (next != 0
                    && depth[next] >= depth[ancestor]
                    && 2 * (dist[start] - dist[next]) <= limit) {
                current = next;
            }
        }

        return current;
    }

    // 간선 (a, b)를 끊었을 때 a가 속한 컴포넌트를 제거
    static void addComponent(int a, int b) {
        if (parent[a] == b) {
            addSubtree(a, 1);
        } else {
            allAdd++;
            addSubtree(b, -1);
        }
    }

    static void addSubtree(int vertex, int value) {
        diff[tin[vertex]] += value;
        diff[tout[vertex] + 1] -= value;
    }

    static long getDistance(int a, int b) {
        int lca = lca(a, b);

        return dist[a] + dist[b] - 2 * dist[lca];
    }

    static int lca(int a, int b) {
        if (depth[a] < depth[b]) {
            int temp = a;
            a = b;
            b = temp;
        }

        for (int k = LOG - 1; k >= 0; k--) {
            if (depth[a] - (1 << k) >= depth[b]) {
                a = up[k][a];
            }
        }

        if (a == b) return a;

        for (int k = LOG - 1; k >= 0; k--) {
            if (up[k][a] != up[k][b]) {
                a = up[k][a];
                b = up[k][b];
            }
        }

        return parent[a];
    }

    static int find(int x, int[] parents) {
        if (parents[x] == x) return x;
        return parents[x] = find(parents[x], parents);
    }

    static boolean union(int a, int b, int[] parents) {
        a = find(a, parents);
        b = find(b, parents);

        if (a == b) return false;

        parents[b] = a;
        return true;
    }
}