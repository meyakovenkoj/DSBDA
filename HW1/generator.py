import argparse
import os
import random
import shutil
import json
import time

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
parser.add_argument('-real', action='store_true',
                    help='use real generate (too long)')
parser.add_argument('-live', action='store_true',
                    help='use real mouse')
parser.add_argument('-users', type=int, default=10, help='count of users')
parser.set_defaults(real=False, live=False)


def realtime(count, x, y):
    overall = 0
    for j in range(3):
        fd = os.open('file.' + str(j), os.O_RDWR | os.O_CREAT)
        text = ""
        for i in range(count):
            overall += i
            if overall % 1000 == 0:
                print("step ", overall//1000)
            pos = pyautogui.position()
            text += str(pos[0] % x) + ',' + \
                str(pos[1] % y) + ',' + \
                str(1647291671 + (i % 1000)) + ',' + \
                str(1000 + (i % 5)) + '\n'
            time.sleep(0.001)
        os.write(fd, text.encode('utf8'))
        os.close(fd)


def generate(count, anchor_x, anchor_y, users, x, y, real):
    for i in range(3):
        fd = os.open('file.' + str(i), os.O_RDWR | os.O_CREAT)
        text = ""
        for j in range(count):
            if real:
                rx = int.from_bytes(os.urandom(20), byteorder="big") % x
                ry = int.from_bytes(os.urandom(20), byteorder="big") % y
                text += str(rx) + ',' + \
                    str(ry) + ',' + \
                    str(1647291671 + (j % 1000)) + ',' + \
                    str(1000 + (j % users)) + '\n'
            else:
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
    current_pwd = os.getcwd()
    input_path = os.path.join(current_pwd, "input")
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
    if args.live:
        import pyautogui
        realtime(size, args.x, args.y)
    else:
        generate(size, anchor_points_x,
                 anchor_points_y, args.users, args.x, args.y, args.real)
    os.chdir(current_pwd)
    with open('SECTORS', 'w') as fd:
        counter = 0
        sectors = dict()
        for j in range(0, args.y, 120):
            for i in range(0, args.x, 120):
                left_c = [i, j]
                right_c = [i + 120, j + 120]
                sectors["SECTOR_{}".format(counter)] = [left_c, right_c]
                counter += 1
        json_object = json.dumps(sectors, indent=4)
        print(json_object, file=fd)

    with open('TEMPERATURE', 'w') as fd:
        temps = dict()
        counter_temp = 1
        for i in range(0, 1500, 10):
            temps[i] = counter_temp
            counter_temp += 1
        json_object = json.dumps(temps, indent=4)
        print(json_object, file=fd)
    print("done")
