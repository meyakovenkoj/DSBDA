#!/usr/bin/python3
import argparse
import os
import random
import shutil

parser = argparse.ArgumentParser(description='Generator of clicks on display')
parser.add_argument('count', metavar='count', type=int,
                    help='amount of log strings')
parser.add_argument('-x', type=int, default=1920, help='width of display')
parser.add_argument('-y', type=int, default=1080, help='height of display')
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


if __name__ == "__main__":
    args = parser.parse_args()
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
    generate(args.count, anchor_points_x,
             anchor_points_y, args.users, args.x, args.y)
    print("done")
