import matplotlib.pyplot as plt
import numpy as np
import argparse

parser = argparse.ArgumentParser(description='Generator of clicks on display')
parser.add_argument('-c', action='store_true', help='console mode')
parser.set_defaults(c=False)


if __name__ == "__main__":
    args = parser.parse_args()
    filepath = './output/part-r-00000'
    marked = dict()
    with open(filepath, "r") as fd:
        lines = fd.readlines()
        for each in lines:
            splited = each.split('\t')
            name = splited[0]
            value = splited[1]
            marked[name] = int(value)
        a = np.zeros((1080//120, 1920//120))
        for key in marked.keys():
            a[int(key[7:]) // 16, int(key[7:]) % 16] = marked[key]

    plt.imshow(a, cmap='hot', interpolation='nearest')
    if args.c:
        plt.savefig('output.png')
    else:
        plt.show()
