#!/usr/bin/python3
import argparse
import os
import random
import shutil

consts = {'c': 1,
          'w': 2,
          'b': 512,
          'kB': 1000,
          'K': 1024,
          'MB': 1000*1000,
          'M': 1024*1024,
          'GB': 1000*1000*1000,
          'G': 1024*1024*1024}

parser = argparse.ArgumentParser(description='Generator of clicks on display')
parser.add_argument('-c', type=int, default=1, help='amount of log strings')
parser.add_argument('-x', type=int, default=1920, help='width of display')
parser.add_argument('-y', type=int, default=1080, help='height of display')
parser.add_argument('-s', type=str, default='1kB', help='size of file')
parser.add_argument('-users', type=int, default=10, help='count of users')


def generate(count, anchor_x, anchor_y, users, x, y):
    for i in range(3):
        fd = os.open('file.' + str(i), os.O_RDWR | os.O_CREAT)
        text = ""
        for j in range(count):
            text += str((anchor_x[j % 10]+(j % 100)) % x) + ',' + \
                str((anchor_y[j % 10]+(j % 100)) % y) + ',' + \
                str(1647291671 + (j % 1000)) + ',' + \
                str(1000 + (j % users)) + '\n'
        os.write(fd, text.encode('utf8'))
        os.close(fd)


def check_consts(size):
    global consts
    for key in consts.keys():
        if size.endswith(key):
            try:
                value = int(size[:-len(key)])
            except ValueError:
                return 0
            finally:
                return value * consts[key] // 24
    return 0


if __name__ == "__main__":
    args = parser.parse_args()
    size = check_consts(args.s)
    input_path = os.path.join(os.getcwd(), "input")
    if os.path.exists(input_path):
        yes = str(input("./input exists. delete it? [y/n]"))
        if yes == "y":
            shutil.rmtree(input_path)
        else:
            print("generation failed")
            exit(1)
    os.mkdir(input_path)
    os.chdir(input_path)
    anchor_points_x = [random.randint(0, args.x) for _ in range(10)]
    anchor_points_y = [random.randint(0, args.y) for _ in range(10)]
    if not size:
        size = args.c
    generate(size, anchor_points_x,
             anchor_points_y, args.users, args.x, args.y)
    print("done")
