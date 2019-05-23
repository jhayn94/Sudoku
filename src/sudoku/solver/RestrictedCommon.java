package sudoku.solver;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Describes Restriced Commons (RCs) between two ALS; since we only handle ALS
 * and not AALS or greater a maximum of 2 RCs between any ALS pair can
 * exist.<br>
 *
 * If only one RC exists for the pair, the second is 0.<br>
 *
 * The references to the ALS are stored as indices into a
 * <code>List&lt;{@link Als}&gt;</code>, they are therefore meaningless outside
 * the scope of the list for which they were created.
 *
 * @author hobiwan
 */
public class RestrictedCommon implements Comparable<RestrictedCommon>, Cloneable {

	/**
	 * Index of first ALS (index into a <code>List&lt;{@link Als}&gt;</code> stored
	 * elsewhere)
	 */
	private int als1;
	/**
	 * Index of the second ALS (index into a <code>List&lt;{@link Als}&gt;</code>
	 * stored elsewhere)
	 */
	private int als2;
	/** First RC, must be != 0. */
	private int cand1;
	/**
	 * Second rc; if <code>cand2 == 0</code> only one rc exists between als1 and
	 * als2
	 */
	private int cand2;
	/**
	 * Used for propagation checks in ALS-Chains (see
	 * {@link AlsSolver#getAlsXYChain()} for details). 0: none, 1: cand1 only, 2:
	 * cand2 only, 3: both.
	 */
	private int actualRC;

	/**
	 * Creates a new instance of <code>RestricteCommon</code>.
	 */
	public RestrictedCommon() {
	}

	/**
	 * Creates a new instance of <code>RestricteCommon</code> for two singly linked
	 * ALS.
	 * 
	 * @param als1
	 * @param als2
	 * @param cand1
	 */
	public RestrictedCommon(int als1, int als2, int cand1) {
		this.als1 = als1;
		this.als2 = als2;
		this.cand1 = cand1;
		this.cand2 = 0;
	}

	/**
	 * Creates a new instance of <code>RestricteCommon</code> for two doubly linked
	 * ALS.
	 * 
	 * @param als1
	 * @param cand2
	 * @param als2
	 * @param cand1
	 */
	public RestrictedCommon(int als1, int als2, int cand1, int cand2) {
		this(als1, als2, cand1);
		this.cand2 = cand2;
	}

	/**
	 * Creates a new instance of <code>RestricteCommon</code> for two ALS and
	 * specifies the actual RC.
	 * 
	 * @param als1
	 * @param als2
	 * @param cand2
	 * @param cand1
	 * @param actualRC
	 */
	public RestrictedCommon(int als1, int als2, int cand1, int cand2, int actualRC) {
		this(als1, als2, cand1, cand2);
		this.actualRC = actualRC;
	}

	/**
	 * New propagation rules for ALS-Chains: the actual RCs of parameter
	 * <code>rc</code> are excluded from <code>this</code>,
	 * <code>this.actualRC</code> is adjusted as necessary; if
	 * <code>this.actualRC</code> is greater than <code>0</code> the chain can be
	 * continued and true is returned, else false is returned.<br>
	 * <br>
	 * 
	 * If a chain starts with a doubly linked RC (<code>rc == null</code>,
	 * <code>cand2 != 0</code>), one of the RCs can be chosen freely; this results
	 * in two different tries for the chain search.
	 * 
	 * @param rc       RC of the previous link in a chain
	 * @param firstTry Only used, if <code>rc == null</code>: if set,
	 *                 <code>cand1</code> is used else <code>cand2</code>
	 * @return true if an actual RC remains, false otherwise
	 */
	public boolean checkRC(RestrictedCommon rc, boolean firstTry) {
		this.actualRC = this.cand2 == 0 ? 1 : 3;
		// rc is not provided
		if (rc == null) {
			// start of chain: pick your RC
			if (this.cand2 != 0) {
				this.actualRC = firstTry ? 1 : 2;
			}
			return this.actualRC != 0;
		}
		switch (rc.actualRC) {
		case 0:
			// already done
			break;
		case 1:
			this.actualRC = this.checkRCInt(rc.cand1, 0, this.cand1, this.cand2);
			break;
		case 2:
			this.actualRC = this.checkRCInt(rc.cand2, 0, this.cand1, this.cand2);
			break;
		case 3:
			this.actualRC = this.checkRCInt(rc.cand1, rc.cand1, this.cand1, this.cand2);
			break;
		default:
			break;
		}
		return this.actualRC != 0;
	}

	/**
	 * Checks duplicates (all possible combinations); <code>c12</code> and
	 * <code>c22</code> can be 0 (meaning: to be ignored).
	 * 
	 * @param c11 First ARC of first link
	 * @param c12 Second ARC of first link (may be 0)
	 * @param c21 First PRC of second link
	 * @param c22 Second PRC of second link (may be 0)
	 * @return
	 */
	private int checkRCInt(int c11, int c12, int c21, int c22) {
		if (c12 == 0) {
			// one ARC
			if (c22 == 0) {
				// one ARC one PRC
				if (c11 == c21) {
					return 0;
				} else {
					return 1;
				}
			} else {
				// one ARC two PRCs
				if (c11 == c22) {
					return 1;
				} else if (c11 == c21) {
					return 2;
				} else {
					return 3;
				}
			}
		} else {
			// two ARCs
			if (c22 == 0) {
				// two ARCs one PRC
				if (c11 == c21 || c12 == c21) {
					return 0;
				} else {
					return 1;
				}
			} else {
				// two ARCs two PRCs
				if ((c11 == c21 && c12 == c22) || (c11 == c22 && c12 == c21)) {
					return 0;
				} else if (c11 == c22 || c12 == c22) {
					return 1;
				} else if (c11 == c21 || c12 == c21) {
					return 2;
				} else {
					return 3;
				}
			}
		}
	}

	/**
	 * Returns a string representation of <code>this</code>.
	 * 
	 * @return
	 */
	@Override
	public String toString() {
		return "RC(" + this.als1 + "/" + this.als2 + "/" + this.cand1 + "/" + this.cand2 + "/" + this.actualRC + ")";
	}

	/**
	 * Compares two RCs.
	 * 
	 * @param r
	 * @return
	 */
	@Override
	public int compareTo(RestrictedCommon r) {
		int result = this.als1 - r.als1;
		if (result == 0) {
			result = this.als2 - r.als2;
			if (result == 0) {
				result = this.cand1 - r.cand1;
				if (result == 0) {
					result = this.cand2 - r.cand2;
				}
			}
		}
		return result;
	}

	/**
	 * Returns a shallow copy of <code>this</code>. Since the class holds only base
	 * types, this is sufficient.
	 * 
	 * @return
	 */
	@Override
	public Object clone() {
		try {
			final RestrictedCommon newRC = (RestrictedCommon) super.clone();
			return newRC;
		} catch (final CloneNotSupportedException ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error while cloning (RC)", ex);
			return null;
		}
	}

	/**
	 * Getter for {@link #als1}.
	 * 
	 * @return
	 */
	public int getAls1() {
		return this.als1;
	}

	/**
	 * Setter for {@link #als1}.
	 * 
	 * @param als1
	 */
	public void setAls1(int als1) {
		this.als1 = als1;
	}

	/**
	 * Getter for {@link #als2}.
	 * 
	 * @return
	 */
	public int getAls2() {
		return this.als2;
	}

	/**
	 * Setter for {@link #als2}.
	 * 
	 * @param als2
	 */
	public void setAls2(int als2) {
		this.als2 = als2;
	}

	/**
	 * Getter for {@link #cand1}.
	 * 
	 * @return
	 */
	public int getCand1() {
		return this.cand1;
	}

	/**
	 * Setter for {@link #cand1}.
	 * 
	 * @param cand1
	 */
	public void setCand1(int cand1) {
		this.cand1 = cand1;
	}

	/**
	 * Getter for {@link #cand2}.
	 * 
	 * @return
	 */
	public int getCand2() {
		return this.cand2;
	}

	/**
	 * Setter for {@link #cand2}.
	 * 
	 * @param cand2
	 */
	public void setCand2(int cand2) {
		this.cand2 = cand2;
	}

	/**
	 * Getter for {@link #actualRC}.
	 * 
	 * @return
	 */
	public int getActualRC() {
		return this.actualRC;
	}

	/**
	 * Setter for {@link #actualRC}.
	 * 
	 * @param actualRC
	 */
	public void setActualRC(int actualRC) {
		this.actualRC = actualRC;
	}

}
