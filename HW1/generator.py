import argparse
import random
import time

parser = argparse.ArgumentParser(description='Generator of clicks on display')
parser.add_argument('count', metavar='count', type=int,
                    help='amount of log strings')
parser.add_argument('-x', type=int, default=1920, help='width of display')
parser.add_argument('-y', type=int, default=1080, help='height of display')
parser.add_argument('-users', type=int, default=10, help='count of users')


class Point:
    def __init__(self, x, y):
        self.x = x
        self.y = y

    def __str__(self):
        return "{0},{1}".format(self.x, self.y)


def get_random(max):
    return random.randint(0, max)


def get_userid(count):
    return (1000 + get_random(count))


def generate(count, x, y, users):
    for _ in range(count):
        p = Point(get_random(x), get_random(y))
        print(p, time.time(), get_userid(users), sep=',')


if __name__ == "__main__":
    args = parser.parse_args()
    generate(args.count, args.x, args.y, args.users)
