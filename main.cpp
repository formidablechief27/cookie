/**
 *    author:  tourist
 *    created: 19.02.2024 09:32:25
**/
#include <bits/stdc++.h>

using namespace std;

#ifdef LOCAL
#include "algo/debug.h"
#else
#define debug(...) 42
#endif

int main() {
  ios::sync_with_stdio(false);
  cin.tie(0);
  int tt;
  cin >> tt;
  while (tt--) {
    string s;
    cin >> s;
    cout << (count(s.begin(), s.end(), 'A') >= 3 ? 'A' : 'B') << '\n';
  }
  return 0;
}