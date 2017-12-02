#!/usr/bin/env python

import csv
import matplotlib.pyplot as plt
import numpy as np
from numpy.linalg import eig
from sklearn.cluster import KMeans
import networkx as nx

def print_edges(edges):
    # Convert to full matrix
    n = max(max(left, right) for left, right, weight in edges)  # Get size of matrix
    matrix = [[-1] * n for i in range(n)]
    for left, right, weight in edges:
        matrix[left - 1][right - 1] = weight  # Convert to 0-based index.

    A = np.matrix(matrix)
    plt.matshow(A)
    plt.show()

path = "./../input/example1.dat"
number_of_clusters = 4

# Read file
with open(path, 'r') as f:
    edges = [map(int, row) for row in csv.reader(f, delimiter=',')]

# create graph
G = nx.Graph()

# Sanitize data
for i in range(0, len(edges)):
    if len(edges[i]) < 2:
        print "Wrong data format"
        exit(2)
    if len(edges[i]) == 2:
        edges[i].append(1)

    # add edges to graph
    G.add_edge(edges[i][0], edges[i][1])

# step 1:
#
# create the adjacency matrix of the graph

matrix = nx.adjacency_matrix(G).todense()
print 'ADJ MATRIX'; print(matrix)

plt.matshow(matrix)
plt.show()

laplacian = nx.laplacian_matrix(G).todense()
eigenvalues, eigenvectors = eig(laplacian)
idx = eigenvalues.argsort()
eigenvalues = eigenvalues[idx]
plt.plot(eigenvalues, 'ro')
plt.suptitle('Eigenvalues (adj matrix)')
plt.show()
plt.plot(eigenvalues[0:10], 'bo')
plt.suptitle('First 10 eigenvalues')
plt.show()
# step 2:
#
# compute the normalised laplacian matrix
norm_laplacian = nx.normalized_laplacian_matrix(G).todense()

# step 3:
#
# compute the eigenvectors and eigenvalues from Laplacian Matrix

norm_eigenvalues, norm_eigenvectors = eig(norm_laplacian)

print 'NORM EIGENVALUES'; print(norm_eigenvalues)
print 'NORM EIGENVECTORS'; print(norm_eigenvectors)

#
# sort the eigenvalues in ascending order and get k largest eigenvectors by its eigenvalues
#
order = norm_eigenvalues.argsort()[::-1][:number_of_clusters]

print 'ORDER'; print(order)

norm_eigenvalues = norm_eigenvalues[order]
norm_eigenvectors = norm_eigenvectors[order,:]

print 'ORDERED K E.VALUES'; print(norm_eigenvalues)
print 'ORDERED K E.VECTORS'; print(norm_eigenvectors)

plt.plot(norm_eigenvalues, 'bo')
plt.suptitle('Laplacian Matrix eigenvalues')
plt.show()

#
# stack the vectors by column
#

col_eigenvectors = norm_eigenvectors.T

print 'TRANSPOSE'; print(col_eigenvectors)

# step 4:
#
# normalise the matrix (L2 norm)
norm = np.linalg.norm(col_eigenvectors, axis=1, ord=2)

norm_col_eigenvectors = col_eigenvectors.astype(np.float) / norm[:,None]
where_are_NaNs = np.isnan(norm_col_eigenvectors)
norm_col_eigenvectors[where_are_NaNs] = 0

print 'NORMALISE'; print(norm_col_eigenvectors)


# Step 5:
#
# # run K means on the normalised matrix
kmeans_model_labels = KMeans(n_clusters=number_of_clusters).fit_predict(matrix)
print 'LABELS'; print kmeans_model_labels

# Step 6:
#
# assign points to clusters

for i in range(0, len(edges)):
    v1 = edges[i][0]
    v2 = edges[i][1]
    v1cluster = kmeans_model_labels[v1-1]
    v2cluster = kmeans_model_labels[v2-1]
    if (v1cluster == v2cluster):
        edges[i][2] = v1cluster


print 'LABELLED EDGES'; print(edges)
data = np.array(edges)

data = data[np.argsort(data[:,2])]
print 'LABELLED EDGES'; print(data)

print_edges(data)