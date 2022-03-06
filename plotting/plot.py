#!/usr/bin/env python3

import pandas as pd
import matplotlib.pyplot as plt

if __name__ == '__main__':
    dfr = pd.read_csv('./plot_data.tsv', sep='\t', index_col=0)
    print(dfr.head())
    
    ax = plt.subplot(3, 1, 1)
    dfr[["max_steps", "mean_steps"]].plot(ax=ax, title='Steps')
    plt.legend(['max', 'mean'])
    plt.xlabel('')

    ax = plt.subplot(3, 1, 2)
    dfr[["max_apples", "mean_apples"]].plot(ax=ax, title='Apples eaten')
    plt.legend(['max', 'mean'])
    plt.xlabel('')

    ax = plt.subplot(3, 1, 3)
    dfr[["max_score", "mean_score"]].plot(ax=ax, title='Score')
    plt.yscale('log')
    plt.legend(['max', 'mean'])
    plt.xlabel('Generation number')


    plt.show()
