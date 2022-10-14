# include <time.h>
# include <stdio.h>
# include <stdlib.h>

struct tm* get_now();
char* time_str(struct tm* input);
struct tm* initialize_tm(int year, int month, int day);
char* add_days_str(int year, int month, int day, int diff);

char* add_days_str(int year, int month, int day, int diff) {
  struct tm* t = initialize_tm(year, month, day);
  t->tm_mday += diff;
  time_t next = mktime(t);

  struct tm *next_tm = localtime(&next);
  return time_str(next_tm);
}

struct tm* initialize_tm(int year, int month, int day) {
  time_t     now;
  struct tm*  t;
  time(&now);
  t = localtime(&now);

  t->tm_year = year - 1900;
  t->tm_mon = month - 1;
  t->tm_mday = day;
  t->tm_hour = 12;
  return t;
}

char* get_now_str() {
    time_t     now;
    struct tm*  time_st;
    time(&now);
    time_st = localtime(&now);
    return time_str(time_st);
}

struct tm* get_now() {
    time_t     now;
    struct tm*  time_st;
    time(&now);
    time_st = localtime(&now);
    return time_st;
}

char* time_str(struct tm* input) {
    int size = sizeof(char) * 80;
    char * buf = malloc(size);
    strftime(buf, size, "%Y %m %d %H %M", input);
    return &buf[0];
}

