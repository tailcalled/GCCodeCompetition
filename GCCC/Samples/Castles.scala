object Castles {

	trait Monoid[M] {
		def op(x: M, y: M): M
		def id: M
	}
	implicit object SumInt extends Monoid[Int] {
		def op(x: Int, y: Int) = x+y
		def id = 0
	}

	class UnionFind[Node, M: Monoid] {
		private var map = Map[Node, Node]()
		private var m = Map[Node, M]()
		def find(x: Node): Node = map.get(x) match {
			case Some(y) if x != y => find(y)
			case Some(y) /* x == y */ => y
			case None =>
				map += ((x, x))
				m += ((x, implicitly[Monoid[M]].id))
				map(x)
		}
		def union(x: Node, y: Node) = {
			val (x0, y0) = (find(x), find(y))
			if (x0 != y0) {
				map += ((x0, y0))
				m += ((y0, implicitly[Monoid[M]].op(m(x0), m(y0))))
				m -= x0
			}
			()
		}
		def op(x: Node, v: M) = {
			val x0 = find(x)
			m += ((x0, implicitly[Monoid[M]].op(m(x0), v)))
		}
		def nodes = map.keys
		def roots = m
	}

	type Module = (Int, Int)
	type Castle = UnionFind[Module, Int]

	def read(): Castle = {
		val res = new Castle()
		val in = io.Source.stdin.getLines
		val (h, w) = (in.next.toInt, in.next.toInt)
		for (y <- 0 until h) {
			val ln = in.next.split(" ")
			for (x <- 0 until w) {
				res.op((x, y), 1)
				val walls = ln(x).toInt
				if ((walls & 4) == 0) res.union((x, y), (x+1, y))
				if ((walls & 8) == 0) res.union((x, y), (x, y+1))
			}
		}
		res
	}

	def main(args: Array[String]) = {
		val castle = read()
		println(castle.roots.size)
		println(castle.roots.map(_._2).max)
		println(
			(for {
				(x, y) <- castle.nodes
				val root = castle.find((x, y))
				dir <- 0 until 2
				val other = castle.find((x+dir, y+(1-dir)))
				if (root != other)
			} yield castle.roots(root) + castle.roots(other)).max
		)
	}

}