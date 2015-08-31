/*
 * This file is part of the JFeatureLib project: https://github.com/locked-fg/JFeatureLib
 * JFeatureLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * JFeatureLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JFeatureLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * You are kindly asked to refer to the papers of the according authors which 
 * should be mentioned in the Javadocs of the respective classes as well as the 
 * JFeatureLib project itself.
 * 
 * Hints how to cite the projects can be found at 
 * https://github.com/locked-fg/JFeatureLib/wiki/Citation
 * 
 * ##########################################################
 * FAST Corner Detector
 * 
 * FAST was first published By Edward Rosten in 2005, 2006:<br>
 * - Fusing points and lines for high performance tracking (2005) and<br>
 * - Machine learning for high-speed corner detection (2006).
 * 
 * Please see http://www.edwardrosten.com/work/fast.html for links to the according papers, more information 
 * and other reference implementations. At this page you can also find BSD-licensed code.
 * 
 * In case of using this code, the above copyright notive must retain the attribution to the author.
 */
package de.lmu.ifi.dbs.jfeaturelib.pointDetector.FAST;

import de.lmu.ifi.dbs.jfeaturelib.ImagePoint;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Robert Zelhofer
 */
public class FAST10 {

    int bmin;
    int b;
    int bmax;
    boolean endScore;

    public List<ImagePoint> fast10_detect_nonmax(int[] im, int xsize, int ysize, int stride, int b, List<ImagePoint> ret_num_corners) {
        List<ImagePoint> corners;
        int num_corners = 0;
        int[] scores;
        List<ImagePoint> nonmax = new ArrayList<>();

        corners = fast10_detect(im, xsize, ysize, stride, b, num_corners);
        scores = fast10_score(im, stride, corners, corners.size(), b);
        FASTNonMaxSuppression nonmaxSuppressor = new FASTNonMaxSuppression();
        nonmax = nonmaxSuppressor.nonmax_suppression(corners, scores, corners.size());

        return nonmax;
    }

    private List<ImagePoint> fast10_detect(int[] im, int xsize, int ysize, int stride, int b, int ret_num_corners) {
        int num_corners = 0;
        List<ImagePoint> ret_corners;
        int[] pixel = new int[16];
        int x, y;

        ret_corners = new ArrayList<>();

        for (y = 3; y < ysize - 3; y++) {
            for (x = 3; x < xsize - 3; x++) {
                FASTUtils.make_offsets(pixel, stride, x, y);
                int[] p = im;
                int pValue = im[y * stride + x];
                int cb = pValue + b;
                int c_b = pValue - b;
                if (p[pixel[0]] > cb) {
                    if (p[pixel[1]] > cb) {
                        if (p[pixel[2]] > cb) {
                            if (p[pixel[3]] > cb) {
                                if (p[pixel[4]] > cb) {
                                    if (p[pixel[5]] > cb) {
                                        if (p[pixel[6]] > cb) {
                                            if (p[pixel[7]] > cb) {
                                                if (p[pixel[8]] > cb) {
                                                    if (p[pixel[9]] > cb) {
                                                    } else if (p[pixel[15]] > cb) {
                                                    } else {
                                                        continue;
                                                    }
                                                } else if (p[pixel[14]] > cb) {
                                                    if (p[pixel[15]] > cb) {
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else if (p[pixel[13]] > cb) {
                                                if (p[pixel[14]] > cb) {
                                                    if (p[pixel[15]] > cb) {
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else if (p[pixel[6]] < c_b) {
                                            if (p[pixel[12]] > cb) {
                                                if (p[pixel[13]] > cb) {
                                                    if (p[pixel[14]] > cb) {
                                                        if (p[pixel[15]] > cb) {
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else if (p[pixel[12]] < c_b) {
                                                if (p[pixel[7]] < c_b) {
                                                    if (p[pixel[8]] < c_b) {
                                                        if (p[pixel[9]] < c_b) {
                                                            if (p[pixel[10]] < c_b) {
                                                                if (p[pixel[11]] < c_b) {
                                                                    if (p[pixel[13]] < c_b) {
                                                                        if (p[pixel[14]] < c_b) {
                                                                            if (p[pixel[15]] < c_b) {
                                                                            } else {
                                                                                continue;
                                                                            }
                                                                        } else {
                                                                            continue;
                                                                        }
                                                                    } else {
                                                                        continue;
                                                                    }
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else if (p[pixel[12]] > cb) {
                                            if (p[pixel[13]] > cb) {
                                                if (p[pixel[14]] > cb) {
                                                    if (p[pixel[15]] > cb) {
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else if (p[pixel[5]] < c_b) {
                                        if (p[pixel[15]] > cb) {
                                            if (p[pixel[11]] > cb) {
                                                if (p[pixel[12]] > cb) {
                                                    if (p[pixel[13]] > cb) {
                                                        if (p[pixel[14]] > cb) {
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else if (p[pixel[11]] < c_b) {
                                                if (p[pixel[6]] < c_b) {
                                                    if (p[pixel[7]] < c_b) {
                                                        if (p[pixel[8]] < c_b) {
                                                            if (p[pixel[9]] < c_b) {
                                                                if (p[pixel[10]] < c_b) {
                                                                    if (p[pixel[12]] < c_b) {
                                                                        if (p[pixel[13]] < c_b) {
                                                                            if (p[pixel[14]] < c_b) {
                                                                            } else {
                                                                                continue;
                                                                            }
                                                                        } else {
                                                                            continue;
                                                                        }
                                                                    } else {
                                                                        continue;
                                                                    }
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else if (p[pixel[6]] < c_b) {
                                            if (p[pixel[7]] < c_b) {
                                                if (p[pixel[8]] < c_b) {
                                                    if (p[pixel[9]] < c_b) {
                                                        if (p[pixel[10]] < c_b) {
                                                            if (p[pixel[11]] < c_b) {
                                                                if (p[pixel[12]] < c_b) {
                                                                    if (p[pixel[13]] < c_b) {
                                                                        if (p[pixel[14]] < c_b) {
                                                                        } else {
                                                                            continue;
                                                                        }
                                                                    } else {
                                                                        continue;
                                                                    }
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else if (p[pixel[11]] > cb) {
                                        if (p[pixel[12]] > cb) {
                                            if (p[pixel[13]] > cb) {
                                                if (p[pixel[14]] > cb) {
                                                    if (p[pixel[15]] > cb) {
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else if (p[pixel[11]] < c_b) {
                                        if (p[pixel[6]] < c_b) {
                                            if (p[pixel[7]] < c_b) {
                                                if (p[pixel[8]] < c_b) {
                                                    if (p[pixel[9]] < c_b) {
                                                        if (p[pixel[10]] < c_b) {
                                                            if (p[pixel[12]] < c_b) {
                                                                if (p[pixel[13]] < c_b) {
                                                                    if (p[pixel[14]] < c_b) {
                                                                        if (p[pixel[15]] < c_b) {
                                                                        } else {
                                                                            continue;
                                                                        }
                                                                    } else {
                                                                        continue;
                                                                    }
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else if (p[pixel[4]] < c_b) {
                                    if (p[pixel[14]] > cb) {
                                        if (p[pixel[10]] > cb) {
                                            if (p[pixel[11]] > cb) {
                                                if (p[pixel[12]] > cb) {
                                                    if (p[pixel[13]] > cb) {
                                                        if (p[pixel[15]] > cb) {
                                                        } else if (p[pixel[5]] > cb) {
                                                            if (p[pixel[6]] > cb) {
                                                                if (p[pixel[7]] > cb) {
                                                                    if (p[pixel[8]] > cb) {
                                                                        if (p[pixel[9]] > cb) {
                                                                        } else {
                                                                            continue;
                                                                        }
                                                                    } else {
                                                                        continue;
                                                                    }
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else if (p[pixel[10]] < c_b) {
                                            if (p[pixel[5]] < c_b) {
                                                if (p[pixel[6]] < c_b) {
                                                    if (p[pixel[7]] < c_b) {
                                                        if (p[pixel[8]] < c_b) {
                                                            if (p[pixel[9]] < c_b) {
                                                                if (p[pixel[11]] < c_b) {
                                                                    if (p[pixel[12]] < c_b) {
                                                                        if (p[pixel[13]] < c_b) {
                                                                        } else {
                                                                            continue;
                                                                        }
                                                                    } else {
                                                                        continue;
                                                                    }
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else if (p[pixel[14]] < c_b) {
                                        if (p[pixel[6]] < c_b) {
                                            if (p[pixel[7]] < c_b) {
                                                if (p[pixel[8]] < c_b) {
                                                    if (p[pixel[9]] < c_b) {
                                                        if (p[pixel[10]] < c_b) {
                                                            if (p[pixel[11]] < c_b) {
                                                                if (p[pixel[12]] < c_b) {
                                                                    if (p[pixel[13]] < c_b) {
                                                                        if (p[pixel[5]] < c_b) {
                                                                        } else if (p[pixel[15]] < c_b) {
                                                                        } else {
                                                                            continue;
                                                                        }
                                                                    } else {
                                                                        continue;
                                                                    }
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else if (p[pixel[5]] < c_b) {
                                        if (p[pixel[6]] < c_b) {
                                            if (p[pixel[7]] < c_b) {
                                                if (p[pixel[8]] < c_b) {
                                                    if (p[pixel[9]] < c_b) {
                                                        if (p[pixel[10]] < c_b) {
                                                            if (p[pixel[11]] < c_b) {
                                                                if (p[pixel[12]] < c_b) {
                                                                    if (p[pixel[13]] < c_b) {
                                                                    } else {
                                                                        continue;
                                                                    }
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else if (p[pixel[10]] > cb) {
                                    if (p[pixel[11]] > cb) {
                                        if (p[pixel[12]] > cb) {
                                            if (p[pixel[13]] > cb) {
                                                if (p[pixel[14]] > cb) {
                                                    if (p[pixel[15]] > cb) {
                                                    } else if (p[pixel[5]] > cb) {
                                                        if (p[pixel[6]] > cb) {
                                                            if (p[pixel[7]] > cb) {
                                                                if (p[pixel[8]] > cb) {
                                                                    if (p[pixel[9]] > cb) {
                                                                    } else {
                                                                        continue;
                                                                    }
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else if (p[pixel[10]] < c_b) {
                                    if (p[pixel[6]] < c_b) {
                                        if (p[pixel[7]] < c_b) {
                                            if (p[pixel[8]] < c_b) {
                                                if (p[pixel[9]] < c_b) {
                                                    if (p[pixel[11]] < c_b) {
                                                        if (p[pixel[12]] < c_b) {
                                                            if (p[pixel[13]] < c_b) {
                                                                if (p[pixel[14]] < c_b) {
                                                                    if (p[pixel[5]] < c_b) {
                                                                    } else if (p[pixel[15]] < c_b) {
                                                                    } else {
                                                                        continue;
                                                                    }
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else {
                                    continue;
                                }
                            } else if (p[pixel[3]] < c_b) {
                                if (p[pixel[9]] > cb) {
                                    if (p[pixel[10]] > cb) {
                                        if (p[pixel[11]] > cb) {
                                            if (p[pixel[12]] > cb) {
                                                if (p[pixel[13]] > cb) {
                                                    if (p[pixel[14]] > cb) {
                                                        if (p[pixel[15]] > cb) {
                                                        } else if (p[pixel[5]] > cb) {
                                                            if (p[pixel[6]] > cb) {
                                                                if (p[pixel[7]] > cb) {
                                                                    if (p[pixel[8]] > cb) {
                                                                    } else {
                                                                        continue;
                                                                    }
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else if (p[pixel[4]] > cb) {
                                                        if (p[pixel[5]] > cb) {
                                                            if (p[pixel[6]] > cb) {
                                                                if (p[pixel[7]] > cb) {
                                                                    if (p[pixel[8]] > cb) {
                                                                    } else {
                                                                        continue;
                                                                    }
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else if (p[pixel[9]] < c_b) {
                                    if (p[pixel[6]] < c_b) {
                                        if (p[pixel[7]] < c_b) {
                                            if (p[pixel[8]] < c_b) {
                                                if (p[pixel[10]] < c_b) {
                                                    if (p[pixel[11]] < c_b) {
                                                        if (p[pixel[12]] < c_b) {
                                                            if (p[pixel[5]] < c_b) {
                                                                if (p[pixel[4]] < c_b) {
                                                                } else if (p[pixel[13]] < c_b) {
                                                                    if (p[pixel[14]] < c_b) {
                                                                    } else {
                                                                        continue;
                                                                    }
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else if (p[pixel[13]] < c_b) {
                                                                if (p[pixel[14]] < c_b) {
                                                                    if (p[pixel[15]] < c_b) {
                                                                    } else {
                                                                        continue;
                                                                    }
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else {
                                    continue;
                                }
                            } else if (p[pixel[9]] > cb) {
                                if (p[pixel[10]] > cb) {
                                    if (p[pixel[11]] > cb) {
                                        if (p[pixel[12]] > cb) {
                                            if (p[pixel[13]] > cb) {
                                                if (p[pixel[14]] > cb) {
                                                    if (p[pixel[15]] > cb) {
                                                    } else if (p[pixel[5]] > cb) {
                                                        if (p[pixel[6]] > cb) {
                                                            if (p[pixel[7]] > cb) {
                                                                if (p[pixel[8]] > cb) {
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else if (p[pixel[4]] > cb) {
                                                    if (p[pixel[5]] > cb) {
                                                        if (p[pixel[6]] > cb) {
                                                            if (p[pixel[7]] > cb) {
                                                                if (p[pixel[8]] > cb) {
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else {
                                    continue;
                                }
                            } else if (p[pixel[9]] < c_b) {
                                if (p[pixel[6]] < c_b) {
                                    if (p[pixel[7]] < c_b) {
                                        if (p[pixel[8]] < c_b) {
                                            if (p[pixel[10]] < c_b) {
                                                if (p[pixel[11]] < c_b) {
                                                    if (p[pixel[12]] < c_b) {
                                                        if (p[pixel[13]] < c_b) {
                                                            if (p[pixel[5]] < c_b) {
                                                                if (p[pixel[4]] < c_b) {
                                                                } else if (p[pixel[14]] < c_b) {
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else if (p[pixel[14]] < c_b) {
                                                                if (p[pixel[15]] < c_b) {
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else {
                                    continue;
                                }
                            } else {
                                continue;
                            }
                        } else if (p[pixel[2]] < c_b) {
                            if (p[pixel[8]] > cb) {
                                if (p[pixel[9]] > cb) {
                                    if (p[pixel[10]] > cb) {
                                        if (p[pixel[11]] > cb) {
                                            if (p[pixel[12]] > cb) {
                                                if (p[pixel[13]] > cb) {
                                                    if (p[pixel[14]] > cb) {
                                                        if (p[pixel[15]] > cb) {
                                                        } else if (p[pixel[5]] > cb) {
                                                            if (p[pixel[6]] > cb) {
                                                                if (p[pixel[7]] > cb) {
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else if (p[pixel[4]] > cb) {
                                                        if (p[pixel[5]] > cb) {
                                                            if (p[pixel[6]] > cb) {
                                                                if (p[pixel[7]] > cb) {
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else if (p[pixel[3]] > cb) {
                                                    if (p[pixel[4]] > cb) {
                                                        if (p[pixel[5]] > cb) {
                                                            if (p[pixel[6]] > cb) {
                                                                if (p[pixel[7]] > cb) {
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else {
                                    continue;
                                }
                            } else if (p[pixel[8]] < c_b) {
                                if (p[pixel[6]] < c_b) {
                                    if (p[pixel[7]] < c_b) {
                                        if (p[pixel[9]] < c_b) {
                                            if (p[pixel[10]] < c_b) {
                                                if (p[pixel[11]] < c_b) {
                                                    if (p[pixel[5]] < c_b) {
                                                        if (p[pixel[4]] < c_b) {
                                                            if (p[pixel[3]] < c_b) {
                                                            } else if (p[pixel[12]] < c_b) {
                                                                if (p[pixel[13]] < c_b) {
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else if (p[pixel[12]] < c_b) {
                                                            if (p[pixel[13]] < c_b) {
                                                                if (p[pixel[14]] < c_b) {
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else if (p[pixel[12]] < c_b) {
                                                        if (p[pixel[13]] < c_b) {
                                                            if (p[pixel[14]] < c_b) {
                                                                if (p[pixel[15]] < c_b) {
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else {
                                    continue;
                                }
                            } else {
                                continue;
                            }
                        } else if (p[pixel[8]] > cb) {
                            if (p[pixel[9]] > cb) {
                                if (p[pixel[10]] > cb) {
                                    if (p[pixel[11]] > cb) {
                                        if (p[pixel[12]] > cb) {
                                            if (p[pixel[13]] > cb) {
                                                if (p[pixel[14]] > cb) {
                                                    if (p[pixel[15]] > cb) {
                                                    } else if (p[pixel[5]] > cb) {
                                                        if (p[pixel[6]] > cb) {
                                                            if (p[pixel[7]] > cb) {
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else if (p[pixel[4]] > cb) {
                                                    if (p[pixel[5]] > cb) {
                                                        if (p[pixel[6]] > cb) {
                                                            if (p[pixel[7]] > cb) {
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else if (p[pixel[3]] > cb) {
                                                if (p[pixel[4]] > cb) {
                                                    if (p[pixel[5]] > cb) {
                                                        if (p[pixel[6]] > cb) {
                                                            if (p[pixel[7]] > cb) {
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else {
                                    continue;
                                }
                            } else {
                                continue;
                            }
                        } else if (p[pixel[8]] < c_b) {
                            if (p[pixel[6]] < c_b) {
                                if (p[pixel[7]] < c_b) {
                                    if (p[pixel[9]] < c_b) {
                                        if (p[pixel[10]] < c_b) {
                                            if (p[pixel[11]] < c_b) {
                                                if (p[pixel[12]] < c_b) {
                                                    if (p[pixel[5]] < c_b) {
                                                        if (p[pixel[4]] < c_b) {
                                                            if (p[pixel[3]] < c_b) {
                                                            } else if (p[pixel[13]] < c_b) {
                                                            } else {
                                                                continue;
                                                            }
                                                        } else if (p[pixel[13]] < c_b) {
                                                            if (p[pixel[14]] < c_b) {
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else if (p[pixel[13]] < c_b) {
                                                        if (p[pixel[14]] < c_b) {
                                                            if (p[pixel[15]] < c_b) {
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else {
                                    continue;
                                }
                            } else {
                                continue;
                            }
                        } else {
                            continue;
                        }
                    } else if (p[pixel[1]] < c_b) {
                        if (p[pixel[7]] > cb) {
                            if (p[pixel[8]] > cb) {
                                if (p[pixel[9]] > cb) {
                                    if (p[pixel[10]] > cb) {
                                        if (p[pixel[11]] > cb) {
                                            if (p[pixel[12]] > cb) {
                                                if (p[pixel[13]] > cb) {
                                                    if (p[pixel[14]] > cb) {
                                                        if (p[pixel[15]] > cb) {
                                                        } else if (p[pixel[5]] > cb) {
                                                            if (p[pixel[6]] > cb) {
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else if (p[pixel[4]] > cb) {
                                                        if (p[pixel[5]] > cb) {
                                                            if (p[pixel[6]] > cb) {
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else if (p[pixel[3]] > cb) {
                                                    if (p[pixel[4]] > cb) {
                                                        if (p[pixel[5]] > cb) {
                                                            if (p[pixel[6]] > cb) {
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else if (p[pixel[2]] > cb) {
                                                if (p[pixel[3]] > cb) {
                                                    if (p[pixel[4]] > cb) {
                                                        if (p[pixel[5]] > cb) {
                                                            if (p[pixel[6]] > cb) {
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else {
                                    continue;
                                }
                            } else {
                                continue;
                            }
                        } else if (p[pixel[7]] < c_b) {
                            if (p[pixel[6]] < c_b) {
                                if (p[pixel[8]] < c_b) {
                                    if (p[pixel[9]] < c_b) {
                                        if (p[pixel[10]] < c_b) {
                                            if (p[pixel[5]] < c_b) {
                                                if (p[pixel[4]] < c_b) {
                                                    if (p[pixel[3]] < c_b) {
                                                        if (p[pixel[2]] < c_b) {
                                                        } else if (p[pixel[11]] < c_b) {
                                                            if (p[pixel[12]] < c_b) {
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else if (p[pixel[11]] < c_b) {
                                                        if (p[pixel[12]] < c_b) {
                                                            if (p[pixel[13]] < c_b) {
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else if (p[pixel[11]] < c_b) {
                                                    if (p[pixel[12]] < c_b) {
                                                        if (p[pixel[13]] < c_b) {
                                                            if (p[pixel[14]] < c_b) {
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else if (p[pixel[11]] < c_b) {
                                                if (p[pixel[12]] < c_b) {
                                                    if (p[pixel[13]] < c_b) {
                                                        if (p[pixel[14]] < c_b) {
                                                            if (p[pixel[15]] < c_b) {
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else {
                                    continue;
                                }
                            } else {
                                continue;
                            }
                        } else {
                            continue;
                        }
                    } else if (p[pixel[7]] > cb) {
                        if (p[pixel[8]] > cb) {
                            if (p[pixel[9]] > cb) {
                                if (p[pixel[10]] > cb) {
                                    if (p[pixel[11]] > cb) {
                                        if (p[pixel[12]] > cb) {
                                            if (p[pixel[13]] > cb) {
                                                if (p[pixel[14]] > cb) {
                                                    if (p[pixel[15]] > cb) {
                                                    } else if (p[pixel[5]] > cb) {
                                                        if (p[pixel[6]] > cb) {
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else if (p[pixel[4]] > cb) {
                                                    if (p[pixel[5]] > cb) {
                                                        if (p[pixel[6]] > cb) {
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else if (p[pixel[3]] > cb) {
                                                if (p[pixel[4]] > cb) {
                                                    if (p[pixel[5]] > cb) {
                                                        if (p[pixel[6]] > cb) {
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else if (p[pixel[2]] > cb) {
                                            if (p[pixel[3]] > cb) {
                                                if (p[pixel[4]] > cb) {
                                                    if (p[pixel[5]] > cb) {
                                                        if (p[pixel[6]] > cb) {
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else {
                                    continue;
                                }
                            } else {
                                continue;
                            }
                        } else {
                            continue;
                        }
                    } else if (p[pixel[7]] < c_b) {
                        if (p[pixel[6]] < c_b) {
                            if (p[pixel[8]] < c_b) {
                                if (p[pixel[9]] < c_b) {
                                    if (p[pixel[10]] < c_b) {
                                        if (p[pixel[11]] < c_b) {
                                            if (p[pixel[5]] < c_b) {
                                                if (p[pixel[4]] < c_b) {
                                                    if (p[pixel[3]] < c_b) {
                                                        if (p[pixel[2]] < c_b) {
                                                        } else if (p[pixel[12]] < c_b) {
                                                        } else {
                                                            continue;
                                                        }
                                                    } else if (p[pixel[12]] < c_b) {
                                                        if (p[pixel[13]] < c_b) {
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else if (p[pixel[12]] < c_b) {
                                                    if (p[pixel[13]] < c_b) {
                                                        if (p[pixel[14]] < c_b) {
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else if (p[pixel[12]] < c_b) {
                                                if (p[pixel[13]] < c_b) {
                                                    if (p[pixel[14]] < c_b) {
                                                        if (p[pixel[15]] < c_b) {
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else {
                                    continue;
                                }
                            } else {
                                continue;
                            }
                        } else {
                            continue;
                        }
                    } else {
                        continue;
                    }
                } else if (p[pixel[0]] < c_b) {
                    if (p[pixel[1]] > cb) {
                        if (p[pixel[7]] > cb) {
                            if (p[pixel[6]] > cb) {
                                if (p[pixel[8]] > cb) {
                                    if (p[pixel[9]] > cb) {
                                        if (p[pixel[10]] > cb) {
                                            if (p[pixel[5]] > cb) {
                                                if (p[pixel[4]] > cb) {
                                                    if (p[pixel[3]] > cb) {
                                                        if (p[pixel[2]] > cb) {
                                                        } else if (p[pixel[11]] > cb) {
                                                            if (p[pixel[12]] > cb) {
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else if (p[pixel[11]] > cb) {
                                                        if (p[pixel[12]] > cb) {
                                                            if (p[pixel[13]] > cb) {
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else if (p[pixel[11]] > cb) {
                                                    if (p[pixel[12]] > cb) {
                                                        if (p[pixel[13]] > cb) {
                                                            if (p[pixel[14]] > cb) {
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else if (p[pixel[11]] > cb) {
                                                if (p[pixel[12]] > cb) {
                                                    if (p[pixel[13]] > cb) {
                                                        if (p[pixel[14]] > cb) {
                                                            if (p[pixel[15]] > cb) {
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else {
                                    continue;
                                }
                            } else {
                                continue;
                            }
                        } else if (p[pixel[7]] < c_b) {
                            if (p[pixel[8]] < c_b) {
                                if (p[pixel[9]] < c_b) {
                                    if (p[pixel[10]] < c_b) {
                                        if (p[pixel[11]] < c_b) {
                                            if (p[pixel[12]] < c_b) {
                                                if (p[pixel[13]] < c_b) {
                                                    if (p[pixel[14]] < c_b) {
                                                        if (p[pixel[15]] < c_b) {
                                                        } else if (p[pixel[5]] < c_b) {
                                                            if (p[pixel[6]] < c_b) {
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else if (p[pixel[4]] < c_b) {
                                                        if (p[pixel[5]] < c_b) {
                                                            if (p[pixel[6]] < c_b) {
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else if (p[pixel[3]] < c_b) {
                                                    if (p[pixel[4]] < c_b) {
                                                        if (p[pixel[5]] < c_b) {
                                                            if (p[pixel[6]] < c_b) {
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else if (p[pixel[2]] < c_b) {
                                                if (p[pixel[3]] < c_b) {
                                                    if (p[pixel[4]] < c_b) {
                                                        if (p[pixel[5]] < c_b) {
                                                            if (p[pixel[6]] < c_b) {
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else {
                                    continue;
                                }
                            } else {
                                continue;
                            }
                        } else {
                            continue;
                        }
                    } else if (p[pixel[1]] < c_b) {
                        if (p[pixel[2]] > cb) {
                            if (p[pixel[8]] > cb) {
                                if (p[pixel[6]] > cb) {
                                    if (p[pixel[7]] > cb) {
                                        if (p[pixel[9]] > cb) {
                                            if (p[pixel[10]] > cb) {
                                                if (p[pixel[11]] > cb) {
                                                    if (p[pixel[5]] > cb) {
                                                        if (p[pixel[4]] > cb) {
                                                            if (p[pixel[3]] > cb) {
                                                            } else if (p[pixel[12]] > cb) {
                                                                if (p[pixel[13]] > cb) {
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else if (p[pixel[12]] > cb) {
                                                            if (p[pixel[13]] > cb) {
                                                                if (p[pixel[14]] > cb) {
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else if (p[pixel[12]] > cb) {
                                                        if (p[pixel[13]] > cb) {
                                                            if (p[pixel[14]] > cb) {
                                                                if (p[pixel[15]] > cb) {
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else {
                                    continue;
                                }
                            } else if (p[pixel[8]] < c_b) {
                                if (p[pixel[9]] < c_b) {
                                    if (p[pixel[10]] < c_b) {
                                        if (p[pixel[11]] < c_b) {
                                            if (p[pixel[12]] < c_b) {
                                                if (p[pixel[13]] < c_b) {
                                                    if (p[pixel[14]] < c_b) {
                                                        if (p[pixel[15]] < c_b) {
                                                        } else if (p[pixel[5]] < c_b) {
                                                            if (p[pixel[6]] < c_b) {
                                                                if (p[pixel[7]] < c_b) {
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else if (p[pixel[4]] < c_b) {
                                                        if (p[pixel[5]] < c_b) {
                                                            if (p[pixel[6]] < c_b) {
                                                                if (p[pixel[7]] < c_b) {
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else if (p[pixel[3]] < c_b) {
                                                    if (p[pixel[4]] < c_b) {
                                                        if (p[pixel[5]] < c_b) {
                                                            if (p[pixel[6]] < c_b) {
                                                                if (p[pixel[7]] < c_b) {
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else {
                                    continue;
                                }
                            } else {
                                continue;
                            }
                        } else if (p[pixel[2]] < c_b) {
                            if (p[pixel[3]] > cb) {
                                if (p[pixel[9]] > cb) {
                                    if (p[pixel[6]] > cb) {
                                        if (p[pixel[7]] > cb) {
                                            if (p[pixel[8]] > cb) {
                                                if (p[pixel[10]] > cb) {
                                                    if (p[pixel[11]] > cb) {
                                                        if (p[pixel[12]] > cb) {
                                                            if (p[pixel[5]] > cb) {
                                                                if (p[pixel[4]] > cb) {
                                                                } else if (p[pixel[13]] > cb) {
                                                                    if (p[pixel[14]] > cb) {
                                                                    } else {
                                                                        continue;
                                                                    }
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else if (p[pixel[13]] > cb) {
                                                                if (p[pixel[14]] > cb) {
                                                                    if (p[pixel[15]] > cb) {
                                                                    } else {
                                                                        continue;
                                                                    }
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else if (p[pixel[9]] < c_b) {
                                    if (p[pixel[10]] < c_b) {
                                        if (p[pixel[11]] < c_b) {
                                            if (p[pixel[12]] < c_b) {
                                                if (p[pixel[13]] < c_b) {
                                                    if (p[pixel[14]] < c_b) {
                                                        if (p[pixel[15]] < c_b) {
                                                        } else if (p[pixel[5]] < c_b) {
                                                            if (p[pixel[6]] < c_b) {
                                                                if (p[pixel[7]] < c_b) {
                                                                    if (p[pixel[8]] < c_b) {
                                                                    } else {
                                                                        continue;
                                                                    }
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else if (p[pixel[4]] < c_b) {
                                                        if (p[pixel[5]] < c_b) {
                                                            if (p[pixel[6]] < c_b) {
                                                                if (p[pixel[7]] < c_b) {
                                                                    if (p[pixel[8]] < c_b) {
                                                                    } else {
                                                                        continue;
                                                                    }
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else {
                                    continue;
                                }
                            } else if (p[pixel[3]] < c_b) {
                                if (p[pixel[4]] > cb) {
                                    if (p[pixel[14]] > cb) {
                                        if (p[pixel[6]] > cb) {
                                            if (p[pixel[7]] > cb) {
                                                if (p[pixel[8]] > cb) {
                                                    if (p[pixel[9]] > cb) {
                                                        if (p[pixel[10]] > cb) {
                                                            if (p[pixel[11]] > cb) {
                                                                if (p[pixel[12]] > cb) {
                                                                    if (p[pixel[13]] > cb) {
                                                                        if (p[pixel[5]] > cb) {
                                                                        } else if (p[pixel[15]] > cb) {
                                                                        } else {
                                                                            continue;
                                                                        }
                                                                    } else {
                                                                        continue;
                                                                    }
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else if (p[pixel[14]] < c_b) {
                                        if (p[pixel[10]] > cb) {
                                            if (p[pixel[5]] > cb) {
                                                if (p[pixel[6]] > cb) {
                                                    if (p[pixel[7]] > cb) {
                                                        if (p[pixel[8]] > cb) {
                                                            if (p[pixel[9]] > cb) {
                                                                if (p[pixel[11]] > cb) {
                                                                    if (p[pixel[12]] > cb) {
                                                                        if (p[pixel[13]] > cb) {
                                                                        } else {
                                                                            continue;
                                                                        }
                                                                    } else {
                                                                        continue;
                                                                    }
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else if (p[pixel[10]] < c_b) {
                                            if (p[pixel[11]] < c_b) {
                                                if (p[pixel[12]] < c_b) {
                                                    if (p[pixel[13]] < c_b) {
                                                        if (p[pixel[15]] < c_b) {
                                                        } else if (p[pixel[5]] < c_b) {
                                                            if (p[pixel[6]] < c_b) {
                                                                if (p[pixel[7]] < c_b) {
                                                                    if (p[pixel[8]] < c_b) {
                                                                        if (p[pixel[9]] < c_b) {
                                                                        } else {
                                                                            continue;
                                                                        }
                                                                    } else {
                                                                        continue;
                                                                    }
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else if (p[pixel[5]] > cb) {
                                        if (p[pixel[6]] > cb) {
                                            if (p[pixel[7]] > cb) {
                                                if (p[pixel[8]] > cb) {
                                                    if (p[pixel[9]] > cb) {
                                                        if (p[pixel[10]] > cb) {
                                                            if (p[pixel[11]] > cb) {
                                                                if (p[pixel[12]] > cb) {
                                                                    if (p[pixel[13]] > cb) {
                                                                    } else {
                                                                        continue;
                                                                    }
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else if (p[pixel[4]] < c_b) {
                                    if (p[pixel[5]] > cb) {
                                        if (p[pixel[15]] < c_b) {
                                            if (p[pixel[11]] > cb) {
                                                if (p[pixel[6]] > cb) {
                                                    if (p[pixel[7]] > cb) {
                                                        if (p[pixel[8]] > cb) {
                                                            if (p[pixel[9]] > cb) {
                                                                if (p[pixel[10]] > cb) {
                                                                    if (p[pixel[12]] > cb) {
                                                                        if (p[pixel[13]] > cb) {
                                                                            if (p[pixel[14]] > cb) {
                                                                            } else {
                                                                                continue;
                                                                            }
                                                                        } else {
                                                                            continue;
                                                                        }
                                                                    } else {
                                                                        continue;
                                                                    }
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else if (p[pixel[11]] < c_b) {
                                                if (p[pixel[12]] < c_b) {
                                                    if (p[pixel[13]] < c_b) {
                                                        if (p[pixel[14]] < c_b) {
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else if (p[pixel[6]] > cb) {
                                            if (p[pixel[7]] > cb) {
                                                if (p[pixel[8]] > cb) {
                                                    if (p[pixel[9]] > cb) {
                                                        if (p[pixel[10]] > cb) {
                                                            if (p[pixel[11]] > cb) {
                                                                if (p[pixel[12]] > cb) {
                                                                    if (p[pixel[13]] > cb) {
                                                                        if (p[pixel[14]] > cb) {
                                                                        } else {
                                                                            continue;
                                                                        }
                                                                    } else {
                                                                        continue;
                                                                    }
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else if (p[pixel[5]] < c_b) {
                                        if (p[pixel[6]] > cb) {
                                            if (p[pixel[12]] > cb) {
                                                if (p[pixel[7]] > cb) {
                                                    if (p[pixel[8]] > cb) {
                                                        if (p[pixel[9]] > cb) {
                                                            if (p[pixel[10]] > cb) {
                                                                if (p[pixel[11]] > cb) {
                                                                    if (p[pixel[13]] > cb) {
                                                                        if (p[pixel[14]] > cb) {
                                                                            if (p[pixel[15]] > cb) {
                                                                            } else {
                                                                                continue;
                                                                            }
                                                                        } else {
                                                                            continue;
                                                                        }
                                                                    } else {
                                                                        continue;
                                                                    }
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else if (p[pixel[12]] < c_b) {
                                                if (p[pixel[13]] < c_b) {
                                                    if (p[pixel[14]] < c_b) {
                                                        if (p[pixel[15]] < c_b) {
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else if (p[pixel[6]] < c_b) {
                                            if (p[pixel[7]] < c_b) {
                                                if (p[pixel[8]] < c_b) {
                                                    if (p[pixel[9]] < c_b) {
                                                    } else if (p[pixel[15]] < c_b) {
                                                    } else {
                                                        continue;
                                                    }
                                                } else if (p[pixel[14]] < c_b) {
                                                    if (p[pixel[15]] < c_b) {
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else if (p[pixel[13]] < c_b) {
                                                if (p[pixel[14]] < c_b) {
                                                    if (p[pixel[15]] < c_b) {
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else if (p[pixel[12]] < c_b) {
                                            if (p[pixel[13]] < c_b) {
                                                if (p[pixel[14]] < c_b) {
                                                    if (p[pixel[15]] < c_b) {
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else if (p[pixel[11]] > cb) {
                                        if (p[pixel[6]] > cb) {
                                            if (p[pixel[7]] > cb) {
                                                if (p[pixel[8]] > cb) {
                                                    if (p[pixel[9]] > cb) {
                                                        if (p[pixel[10]] > cb) {
                                                            if (p[pixel[12]] > cb) {
                                                                if (p[pixel[13]] > cb) {
                                                                    if (p[pixel[14]] > cb) {
                                                                        if (p[pixel[15]] > cb) {
                                                                        } else {
                                                                            continue;
                                                                        }
                                                                    } else {
                                                                        continue;
                                                                    }
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else if (p[pixel[11]] < c_b) {
                                        if (p[pixel[12]] < c_b) {
                                            if (p[pixel[13]] < c_b) {
                                                if (p[pixel[14]] < c_b) {
                                                    if (p[pixel[15]] < c_b) {
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else if (p[pixel[10]] > cb) {
                                    if (p[pixel[6]] > cb) {
                                        if (p[pixel[7]] > cb) {
                                            if (p[pixel[8]] > cb) {
                                                if (p[pixel[9]] > cb) {
                                                    if (p[pixel[11]] > cb) {
                                                        if (p[pixel[12]] > cb) {
                                                            if (p[pixel[13]] > cb) {
                                                                if (p[pixel[14]] > cb) {
                                                                    if (p[pixel[5]] > cb) {
                                                                    } else if (p[pixel[15]] > cb) {
                                                                    } else {
                                                                        continue;
                                                                    }
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else if (p[pixel[10]] < c_b) {
                                    if (p[pixel[11]] < c_b) {
                                        if (p[pixel[12]] < c_b) {
                                            if (p[pixel[13]] < c_b) {
                                                if (p[pixel[14]] < c_b) {
                                                    if (p[pixel[15]] < c_b) {
                                                    } else if (p[pixel[5]] < c_b) {
                                                        if (p[pixel[6]] < c_b) {
                                                            if (p[pixel[7]] < c_b) {
                                                                if (p[pixel[8]] < c_b) {
                                                                    if (p[pixel[9]] < c_b) {
                                                                    } else {
                                                                        continue;
                                                                    }
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else {
                                    continue;
                                }
                            } else if (p[pixel[9]] > cb) {
                                if (p[pixel[6]] > cb) {
                                    if (p[pixel[7]] > cb) {
                                        if (p[pixel[8]] > cb) {
                                            if (p[pixel[10]] > cb) {
                                                if (p[pixel[11]] > cb) {
                                                    if (p[pixel[12]] > cb) {
                                                        if (p[pixel[13]] > cb) {
                                                            if (p[pixel[5]] > cb) {
                                                                if (p[pixel[4]] > cb) {
                                                                } else if (p[pixel[14]] > cb) {
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else if (p[pixel[14]] > cb) {
                                                                if (p[pixel[15]] > cb) {
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else {
                                    continue;
                                }
                            } else if (p[pixel[9]] < c_b) {
                                if (p[pixel[10]] < c_b) {
                                    if (p[pixel[11]] < c_b) {
                                        if (p[pixel[12]] < c_b) {
                                            if (p[pixel[13]] < c_b) {
                                                if (p[pixel[14]] < c_b) {
                                                    if (p[pixel[15]] < c_b) {
                                                    } else if (p[pixel[5]] < c_b) {
                                                        if (p[pixel[6]] < c_b) {
                                                            if (p[pixel[7]] < c_b) {
                                                                if (p[pixel[8]] < c_b) {
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else if (p[pixel[4]] < c_b) {
                                                    if (p[pixel[5]] < c_b) {
                                                        if (p[pixel[6]] < c_b) {
                                                            if (p[pixel[7]] < c_b) {
                                                                if (p[pixel[8]] < c_b) {
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else {
                                    continue;
                                }
                            } else {
                                continue;
                            }
                        } else if (p[pixel[8]] > cb) {
                            if (p[pixel[6]] > cb) {
                                if (p[pixel[7]] > cb) {
                                    if (p[pixel[9]] > cb) {
                                        if (p[pixel[10]] > cb) {
                                            if (p[pixel[11]] > cb) {
                                                if (p[pixel[12]] > cb) {
                                                    if (p[pixel[5]] > cb) {
                                                        if (p[pixel[4]] > cb) {
                                                            if (p[pixel[3]] > cb) {
                                                            } else if (p[pixel[13]] > cb) {
                                                            } else {
                                                                continue;
                                                            }
                                                        } else if (p[pixel[13]] > cb) {
                                                            if (p[pixel[14]] > cb) {
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else if (p[pixel[13]] > cb) {
                                                        if (p[pixel[14]] > cb) {
                                                            if (p[pixel[15]] > cb) {
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else {
                                    continue;
                                }
                            } else {
                                continue;
                            }
                        } else if (p[pixel[8]] < c_b) {
                            if (p[pixel[9]] < c_b) {
                                if (p[pixel[10]] < c_b) {
                                    if (p[pixel[11]] < c_b) {
                                        if (p[pixel[12]] < c_b) {
                                            if (p[pixel[13]] < c_b) {
                                                if (p[pixel[14]] < c_b) {
                                                    if (p[pixel[15]] < c_b) {
                                                    } else if (p[pixel[5]] < c_b) {
                                                        if (p[pixel[6]] < c_b) {
                                                            if (p[pixel[7]] < c_b) {
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else if (p[pixel[4]] < c_b) {
                                                    if (p[pixel[5]] < c_b) {
                                                        if (p[pixel[6]] < c_b) {
                                                            if (p[pixel[7]] < c_b) {
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else if (p[pixel[3]] < c_b) {
                                                if (p[pixel[4]] < c_b) {
                                                    if (p[pixel[5]] < c_b) {
                                                        if (p[pixel[6]] < c_b) {
                                                            if (p[pixel[7]] < c_b) {
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else {
                                    continue;
                                }
                            } else {
                                continue;
                            }
                        } else {
                            continue;
                        }
                    } else if (p[pixel[7]] > cb) {
                        if (p[pixel[6]] > cb) {
                            if (p[pixel[8]] > cb) {
                                if (p[pixel[9]] > cb) {
                                    if (p[pixel[10]] > cb) {
                                        if (p[pixel[11]] > cb) {
                                            if (p[pixel[5]] > cb) {
                                                if (p[pixel[4]] > cb) {
                                                    if (p[pixel[3]] > cb) {
                                                        if (p[pixel[2]] > cb) {
                                                        } else if (p[pixel[12]] > cb) {
                                                        } else {
                                                            continue;
                                                        }
                                                    } else if (p[pixel[12]] > cb) {
                                                        if (p[pixel[13]] > cb) {
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else if (p[pixel[12]] > cb) {
                                                    if (p[pixel[13]] > cb) {
                                                        if (p[pixel[14]] > cb) {
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else if (p[pixel[12]] > cb) {
                                                if (p[pixel[13]] > cb) {
                                                    if (p[pixel[14]] > cb) {
                                                        if (p[pixel[15]] > cb) {
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else {
                                    continue;
                                }
                            } else {
                                continue;
                            }
                        } else {
                            continue;
                        }
                    } else if (p[pixel[7]] < c_b) {
                        if (p[pixel[8]] < c_b) {
                            if (p[pixel[9]] < c_b) {
                                if (p[pixel[10]] < c_b) {
                                    if (p[pixel[11]] < c_b) {
                                        if (p[pixel[12]] < c_b) {
                                            if (p[pixel[13]] < c_b) {
                                                if (p[pixel[14]] < c_b) {
                                                    if (p[pixel[15]] < c_b) {
                                                    } else if (p[pixel[5]] < c_b) {
                                                        if (p[pixel[6]] < c_b) {
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else if (p[pixel[4]] < c_b) {
                                                    if (p[pixel[5]] < c_b) {
                                                        if (p[pixel[6]] < c_b) {
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else if (p[pixel[3]] < c_b) {
                                                if (p[pixel[4]] < c_b) {
                                                    if (p[pixel[5]] < c_b) {
                                                        if (p[pixel[6]] < c_b) {
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else if (p[pixel[2]] < c_b) {
                                            if (p[pixel[3]] < c_b) {
                                                if (p[pixel[4]] < c_b) {
                                                    if (p[pixel[5]] < c_b) {
                                                        if (p[pixel[6]] < c_b) {
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else {
                                    continue;
                                }
                            } else {
                                continue;
                            }
                        } else {
                            continue;
                        }
                    } else {
                        continue;
                    }
                } else if (p[pixel[6]] > cb) {
                    if (p[pixel[7]] > cb) {
                        if (p[pixel[8]] > cb) {
                            if (p[pixel[9]] > cb) {
                                if (p[pixel[10]] > cb) {
                                    if (p[pixel[5]] > cb) {
                                        if (p[pixel[4]] > cb) {
                                            if (p[pixel[3]] > cb) {
                                                if (p[pixel[2]] > cb) {
                                                    if (p[pixel[1]] > cb) {
                                                    } else if (p[pixel[11]] > cb) {
                                                    } else {
                                                        continue;
                                                    }
                                                } else if (p[pixel[11]] > cb) {
                                                    if (p[pixel[12]] > cb) {
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else if (p[pixel[11]] > cb) {
                                                if (p[pixel[12]] > cb) {
                                                    if (p[pixel[13]] > cb) {
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else if (p[pixel[11]] > cb) {
                                            if (p[pixel[12]] > cb) {
                                                if (p[pixel[13]] > cb) {
                                                    if (p[pixel[14]] > cb) {
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else if (p[pixel[11]] > cb) {
                                        if (p[pixel[12]] > cb) {
                                            if (p[pixel[13]] > cb) {
                                                if (p[pixel[14]] > cb) {
                                                    if (p[pixel[15]] > cb) {
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else {
                                    continue;
                                }
                            } else {
                                continue;
                            }
                        } else {
                            continue;
                        }
                    } else {
                        continue;
                    }
                } else if (p[pixel[6]] < c_b) {
                    if (p[pixel[7]] < c_b) {
                        if (p[pixel[8]] < c_b) {
                            if (p[pixel[9]] < c_b) {
                                if (p[pixel[10]] < c_b) {
                                    if (p[pixel[5]] < c_b) {
                                        if (p[pixel[4]] < c_b) {
                                            if (p[pixel[3]] < c_b) {
                                                if (p[pixel[2]] < c_b) {
                                                    if (p[pixel[1]] < c_b) {
                                                    } else if (p[pixel[11]] < c_b) {
                                                    } else {
                                                        continue;
                                                    }
                                                } else if (p[pixel[11]] < c_b) {
                                                    if (p[pixel[12]] < c_b) {
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else if (p[pixel[11]] < c_b) {
                                                if (p[pixel[12]] < c_b) {
                                                    if (p[pixel[13]] < c_b) {
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else if (p[pixel[11]] < c_b) {
                                            if (p[pixel[12]] < c_b) {
                                                if (p[pixel[13]] < c_b) {
                                                    if (p[pixel[14]] < c_b) {
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else if (p[pixel[11]] < c_b) {
                                        if (p[pixel[12]] < c_b) {
                                            if (p[pixel[13]] < c_b) {
                                                if (p[pixel[14]] < c_b) {
                                                    if (p[pixel[15]] < c_b) {
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else {
                                    continue;
                                }
                            } else {
                                continue;
                            }
                        } else {
                            continue;
                        }
                    } else {
                        continue;
                    }
                } else {
                    continue;
                }
                /*if(num_corners == rsize)
                {
                rsize*=2;
                ret_corners = (xy*)realloc(ret_corners, sizeof(xy)*rsize);
                }
                
                ret_corners[num_corners].x = x;
                ret_corners[num_corners].y = y;
                num_corners++;
                 * 
                 */
                ret_corners.add(new ImagePoint(x, y));
                num_corners++;
                ret_num_corners++;
            }
        }

        ret_num_corners = num_corners;
        return ret_corners;

    }

    private int[] fast10_score(int[] i, int stride, List<ImagePoint> corners, int num_corners, int b) {
        int[] scores = new int[num_corners];
        int n;

        int[] pixel = new int[16];

        for (n = 0; n < num_corners; n++) {
            FASTUtils.make_offsets(pixel, stride, (int) corners.get(n).x, (int) corners.get(n).y);

            scores[n] = fast10_corner_score(i, pixel, b, corners.get(n), stride);
        }
        return scores;
    }

    private void is_a_corner() {
        bmin = b;
        end_if();
    }

    private void is_not_a_corner() {
        bmax = b;
        end_if();
    }

    private void end_if() {
        if (bmin == bmax - 1 || bmin == bmax) {
            endScore = true;
        }
        b = (bmin + bmax) / 2;
    }

    private int fast10_corner_score(int[] p, int[] pixel, int bstart, ImagePoint currentP, int stride) {
        bmin = bstart;
        bmax = 255;
        b = (bmax + bmin) / 2;
        endScore = false;

        /*Compute the score using binary search*/
        while (!endScore) {
            int cb = p[(int) currentP.y * stride + (int) currentP.x] + b;
            int c_b = p[(int) currentP.y * stride + (int) currentP.x] - b;


            if (p[pixel[0]] > cb) {
                if (p[pixel[1]] > cb) {
                    if (p[pixel[2]] > cb) {
                        if (p[pixel[3]] > cb) {
                            if (p[pixel[4]] > cb) {
                                if (p[pixel[5]] > cb) {
                                    if (p[pixel[6]] > cb) {
                                        if (p[pixel[7]] > cb) {
                                            if (p[pixel[8]] > cb) {
                                                if (p[pixel[9]] > cb) {
                                                    is_a_corner();
                                                } else if (p[pixel[15]] > cb) {
                                                    is_a_corner();
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else if (p[pixel[14]] > cb) {
                                                if (p[pixel[15]] > cb) {
                                                    is_a_corner();
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else if (p[pixel[13]] > cb) {
                                            if (p[pixel[14]] > cb) {
                                                if (p[pixel[15]] > cb) {
                                                    is_a_corner();
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else if (p[pixel[6]] < c_b) {
                                        if (p[pixel[12]] > cb) {
                                            if (p[pixel[13]] > cb) {
                                                if (p[pixel[14]] > cb) {
                                                    if (p[pixel[15]] > cb) {
                                                        is_a_corner();
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else if (p[pixel[12]] < c_b) {
                                            if (p[pixel[7]] < c_b) {
                                                if (p[pixel[8]] < c_b) {
                                                    if (p[pixel[9]] < c_b) {
                                                        if (p[pixel[10]] < c_b) {
                                                            if (p[pixel[11]] < c_b) {
                                                                if (p[pixel[13]] < c_b) {
                                                                    if (p[pixel[14]] < c_b) {
                                                                        if (p[pixel[15]] < c_b) {
                                                                            is_a_corner();
                                                                        } else {
                                                                            is_not_a_corner();
                                                                        }
                                                                    } else {
                                                                        is_not_a_corner();
                                                                    }
                                                                } else {
                                                                    is_not_a_corner();
                                                                }
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else if (p[pixel[12]] > cb) {
                                        if (p[pixel[13]] > cb) {
                                            if (p[pixel[14]] > cb) {
                                                if (p[pixel[15]] > cb) {
                                                    is_a_corner();
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else if (p[pixel[5]] < c_b) {
                                    if (p[pixel[15]] > cb) {
                                        if (p[pixel[11]] > cb) {
                                            if (p[pixel[12]] > cb) {
                                                if (p[pixel[13]] > cb) {
                                                    if (p[pixel[14]] > cb) {
                                                        is_a_corner();
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else if (p[pixel[11]] < c_b) {
                                            if (p[pixel[6]] < c_b) {
                                                if (p[pixel[7]] < c_b) {
                                                    if (p[pixel[8]] < c_b) {
                                                        if (p[pixel[9]] < c_b) {
                                                            if (p[pixel[10]] < c_b) {
                                                                if (p[pixel[12]] < c_b) {
                                                                    if (p[pixel[13]] < c_b) {
                                                                        if (p[pixel[14]] < c_b) {
                                                                            is_a_corner();
                                                                        } else {
                                                                            is_not_a_corner();
                                                                        }
                                                                    } else {
                                                                        is_not_a_corner();
                                                                    }
                                                                } else {
                                                                    is_not_a_corner();
                                                                }
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else if (p[pixel[6]] < c_b) {
                                        if (p[pixel[7]] < c_b) {
                                            if (p[pixel[8]] < c_b) {
                                                if (p[pixel[9]] < c_b) {
                                                    if (p[pixel[10]] < c_b) {
                                                        if (p[pixel[11]] < c_b) {
                                                            if (p[pixel[12]] < c_b) {
                                                                if (p[pixel[13]] < c_b) {
                                                                    if (p[pixel[14]] < c_b) {
                                                                        is_a_corner();
                                                                    } else {
                                                                        is_not_a_corner();
                                                                    }
                                                                } else {
                                                                    is_not_a_corner();
                                                                }
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else if (p[pixel[11]] > cb) {
                                    if (p[pixel[12]] > cb) {
                                        if (p[pixel[13]] > cb) {
                                            if (p[pixel[14]] > cb) {
                                                if (p[pixel[15]] > cb) {
                                                    is_a_corner();
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else if (p[pixel[11]] < c_b) {
                                    if (p[pixel[6]] < c_b) {
                                        if (p[pixel[7]] < c_b) {
                                            if (p[pixel[8]] < c_b) {
                                                if (p[pixel[9]] < c_b) {
                                                    if (p[pixel[10]] < c_b) {
                                                        if (p[pixel[12]] < c_b) {
                                                            if (p[pixel[13]] < c_b) {
                                                                if (p[pixel[14]] < c_b) {
                                                                    if (p[pixel[15]] < c_b) {
                                                                        is_a_corner();
                                                                    } else {
                                                                        is_not_a_corner();
                                                                    }
                                                                } else {
                                                                    is_not_a_corner();
                                                                }
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else {
                                    is_not_a_corner();
                                }
                            } else if (p[pixel[4]] < c_b) {
                                if (p[pixel[14]] > cb) {
                                    if (p[pixel[10]] > cb) {
                                        if (p[pixel[11]] > cb) {
                                            if (p[pixel[12]] > cb) {
                                                if (p[pixel[13]] > cb) {
                                                    if (p[pixel[15]] > cb) {
                                                        is_a_corner();
                                                    } else if (p[pixel[5]] > cb) {
                                                        if (p[pixel[6]] > cb) {
                                                            if (p[pixel[7]] > cb) {
                                                                if (p[pixel[8]] > cb) {
                                                                    if (p[pixel[9]] > cb) {
                                                                        is_a_corner();
                                                                    } else {
                                                                        is_not_a_corner();
                                                                    }
                                                                } else {
                                                                    is_not_a_corner();
                                                                }
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else if (p[pixel[10]] < c_b) {
                                        if (p[pixel[5]] < c_b) {
                                            if (p[pixel[6]] < c_b) {
                                                if (p[pixel[7]] < c_b) {
                                                    if (p[pixel[8]] < c_b) {
                                                        if (p[pixel[9]] < c_b) {
                                                            if (p[pixel[11]] < c_b) {
                                                                if (p[pixel[12]] < c_b) {
                                                                    if (p[pixel[13]] < c_b) {
                                                                        is_a_corner();
                                                                    } else {
                                                                        is_not_a_corner();
                                                                    }
                                                                } else {
                                                                    is_not_a_corner();
                                                                }
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else if (p[pixel[14]] < c_b) {
                                    if (p[pixel[6]] < c_b) {
                                        if (p[pixel[7]] < c_b) {
                                            if (p[pixel[8]] < c_b) {
                                                if (p[pixel[9]] < c_b) {
                                                    if (p[pixel[10]] < c_b) {
                                                        if (p[pixel[11]] < c_b) {
                                                            if (p[pixel[12]] < c_b) {
                                                                if (p[pixel[13]] < c_b) {
                                                                    if (p[pixel[5]] < c_b) {
                                                                        is_a_corner();
                                                                    } else if (p[pixel[15]] < c_b) {
                                                                        is_a_corner();
                                                                    } else {
                                                                        is_not_a_corner();
                                                                    }
                                                                } else {
                                                                    is_not_a_corner();
                                                                }
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else if (p[pixel[5]] < c_b) {
                                    if (p[pixel[6]] < c_b) {
                                        if (p[pixel[7]] < c_b) {
                                            if (p[pixel[8]] < c_b) {
                                                if (p[pixel[9]] < c_b) {
                                                    if (p[pixel[10]] < c_b) {
                                                        if (p[pixel[11]] < c_b) {
                                                            if (p[pixel[12]] < c_b) {
                                                                if (p[pixel[13]] < c_b) {
                                                                    is_a_corner();
                                                                } else {
                                                                    is_not_a_corner();
                                                                }
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else {
                                    is_not_a_corner();
                                }
                            } else if (p[pixel[10]] > cb) {
                                if (p[pixel[11]] > cb) {
                                    if (p[pixel[12]] > cb) {
                                        if (p[pixel[13]] > cb) {
                                            if (p[pixel[14]] > cb) {
                                                if (p[pixel[15]] > cb) {
                                                    is_a_corner();
                                                } else if (p[pixel[5]] > cb) {
                                                    if (p[pixel[6]] > cb) {
                                                        if (p[pixel[7]] > cb) {
                                                            if (p[pixel[8]] > cb) {
                                                                if (p[pixel[9]] > cb) {
                                                                    is_a_corner();
                                                                } else {
                                                                    is_not_a_corner();
                                                                }
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else {
                                    is_not_a_corner();
                                }
                            } else if (p[pixel[10]] < c_b) {
                                if (p[pixel[6]] < c_b) {
                                    if (p[pixel[7]] < c_b) {
                                        if (p[pixel[8]] < c_b) {
                                            if (p[pixel[9]] < c_b) {
                                                if (p[pixel[11]] < c_b) {
                                                    if (p[pixel[12]] < c_b) {
                                                        if (p[pixel[13]] < c_b) {
                                                            if (p[pixel[14]] < c_b) {
                                                                if (p[pixel[5]] < c_b) {
                                                                    is_a_corner();
                                                                } else if (p[pixel[15]] < c_b) {
                                                                    is_a_corner();
                                                                } else {
                                                                    is_not_a_corner();
                                                                }
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else {
                                    is_not_a_corner();
                                }
                            } else {
                                is_not_a_corner();
                            }
                        } else if (p[pixel[3]] < c_b) {
                            if (p[pixel[9]] > cb) {
                                if (p[pixel[10]] > cb) {
                                    if (p[pixel[11]] > cb) {
                                        if (p[pixel[12]] > cb) {
                                            if (p[pixel[13]] > cb) {
                                                if (p[pixel[14]] > cb) {
                                                    if (p[pixel[15]] > cb) {
                                                        is_a_corner();
                                                    } else if (p[pixel[5]] > cb) {
                                                        if (p[pixel[6]] > cb) {
                                                            if (p[pixel[7]] > cb) {
                                                                if (p[pixel[8]] > cb) {
                                                                    is_a_corner();
                                                                } else {
                                                                    is_not_a_corner();
                                                                }
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else if (p[pixel[4]] > cb) {
                                                    if (p[pixel[5]] > cb) {
                                                        if (p[pixel[6]] > cb) {
                                                            if (p[pixel[7]] > cb) {
                                                                if (p[pixel[8]] > cb) {
                                                                    is_a_corner();
                                                                } else {
                                                                    is_not_a_corner();
                                                                }
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else {
                                    is_not_a_corner();
                                }
                            } else if (p[pixel[9]] < c_b) {
                                if (p[pixel[6]] < c_b) {
                                    if (p[pixel[7]] < c_b) {
                                        if (p[pixel[8]] < c_b) {
                                            if (p[pixel[10]] < c_b) {
                                                if (p[pixel[11]] < c_b) {
                                                    if (p[pixel[12]] < c_b) {
                                                        if (p[pixel[5]] < c_b) {
                                                            if (p[pixel[4]] < c_b) {
                                                                is_a_corner();
                                                            } else if (p[pixel[13]] < c_b) {
                                                                if (p[pixel[14]] < c_b) {
                                                                    is_a_corner();
                                                                } else {
                                                                    is_not_a_corner();
                                                                }
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else if (p[pixel[13]] < c_b) {
                                                            if (p[pixel[14]] < c_b) {
                                                                if (p[pixel[15]] < c_b) {
                                                                    is_a_corner();
                                                                } else {
                                                                    is_not_a_corner();
                                                                }
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else {
                                    is_not_a_corner();
                                }
                            } else {
                                is_not_a_corner();
                            }
                        } else if (p[pixel[9]] > cb) {
                            if (p[pixel[10]] > cb) {
                                if (p[pixel[11]] > cb) {
                                    if (p[pixel[12]] > cb) {
                                        if (p[pixel[13]] > cb) {
                                            if (p[pixel[14]] > cb) {
                                                if (p[pixel[15]] > cb) {
                                                    is_a_corner();
                                                } else if (p[pixel[5]] > cb) {
                                                    if (p[pixel[6]] > cb) {
                                                        if (p[pixel[7]] > cb) {
                                                            if (p[pixel[8]] > cb) {
                                                                is_a_corner();
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else if (p[pixel[4]] > cb) {
                                                if (p[pixel[5]] > cb) {
                                                    if (p[pixel[6]] > cb) {
                                                        if (p[pixel[7]] > cb) {
                                                            if (p[pixel[8]] > cb) {
                                                                is_a_corner();
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else {
                                    is_not_a_corner();
                                }
                            } else {
                                is_not_a_corner();
                            }
                        } else if (p[pixel[9]] < c_b) {
                            if (p[pixel[6]] < c_b) {
                                if (p[pixel[7]] < c_b) {
                                    if (p[pixel[8]] < c_b) {
                                        if (p[pixel[10]] < c_b) {
                                            if (p[pixel[11]] < c_b) {
                                                if (p[pixel[12]] < c_b) {
                                                    if (p[pixel[13]] < c_b) {
                                                        if (p[pixel[5]] < c_b) {
                                                            if (p[pixel[4]] < c_b) {
                                                                is_a_corner();
                                                            } else if (p[pixel[14]] < c_b) {
                                                                is_a_corner();
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else if (p[pixel[14]] < c_b) {
                                                            if (p[pixel[15]] < c_b) {
                                                                is_a_corner();
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else {
                                    is_not_a_corner();
                                }
                            } else {
                                is_not_a_corner();
                            }
                        } else {
                            is_not_a_corner();
                        }
                    } else if (p[pixel[2]] < c_b) {
                        if (p[pixel[8]] > cb) {
                            if (p[pixel[9]] > cb) {
                                if (p[pixel[10]] > cb) {
                                    if (p[pixel[11]] > cb) {
                                        if (p[pixel[12]] > cb) {
                                            if (p[pixel[13]] > cb) {
                                                if (p[pixel[14]] > cb) {
                                                    if (p[pixel[15]] > cb) {
                                                        is_a_corner();
                                                    } else if (p[pixel[5]] > cb) {
                                                        if (p[pixel[6]] > cb) {
                                                            if (p[pixel[7]] > cb) {
                                                                is_a_corner();
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else if (p[pixel[4]] > cb) {
                                                    if (p[pixel[5]] > cb) {
                                                        if (p[pixel[6]] > cb) {
                                                            if (p[pixel[7]] > cb) {
                                                                is_a_corner();
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else if (p[pixel[3]] > cb) {
                                                if (p[pixel[4]] > cb) {
                                                    if (p[pixel[5]] > cb) {
                                                        if (p[pixel[6]] > cb) {
                                                            if (p[pixel[7]] > cb) {
                                                                is_a_corner();
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else {
                                    is_not_a_corner();
                                }
                            } else {
                                is_not_a_corner();
                            }
                        } else if (p[pixel[8]] < c_b) {
                            if (p[pixel[6]] < c_b) {
                                if (p[pixel[7]] < c_b) {
                                    if (p[pixel[9]] < c_b) {
                                        if (p[pixel[10]] < c_b) {
                                            if (p[pixel[11]] < c_b) {
                                                if (p[pixel[5]] < c_b) {
                                                    if (p[pixel[4]] < c_b) {
                                                        if (p[pixel[3]] < c_b) {
                                                            is_a_corner();
                                                        } else if (p[pixel[12]] < c_b) {
                                                            if (p[pixel[13]] < c_b) {
                                                                is_a_corner();
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else if (p[pixel[12]] < c_b) {
                                                        if (p[pixel[13]] < c_b) {
                                                            if (p[pixel[14]] < c_b) {
                                                                is_a_corner();
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else if (p[pixel[12]] < c_b) {
                                                    if (p[pixel[13]] < c_b) {
                                                        if (p[pixel[14]] < c_b) {
                                                            if (p[pixel[15]] < c_b) {
                                                                is_a_corner();
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else {
                                    is_not_a_corner();
                                }
                            } else {
                                is_not_a_corner();
                            }
                        } else {
                            is_not_a_corner();
                        }
                    } else if (p[pixel[8]] > cb) {
                        if (p[pixel[9]] > cb) {
                            if (p[pixel[10]] > cb) {
                                if (p[pixel[11]] > cb) {
                                    if (p[pixel[12]] > cb) {
                                        if (p[pixel[13]] > cb) {
                                            if (p[pixel[14]] > cb) {
                                                if (p[pixel[15]] > cb) {
                                                    is_a_corner();
                                                } else if (p[pixel[5]] > cb) {
                                                    if (p[pixel[6]] > cb) {
                                                        if (p[pixel[7]] > cb) {
                                                            is_a_corner();
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else if (p[pixel[4]] > cb) {
                                                if (p[pixel[5]] > cb) {
                                                    if (p[pixel[6]] > cb) {
                                                        if (p[pixel[7]] > cb) {
                                                            is_a_corner();
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else if (p[pixel[3]] > cb) {
                                            if (p[pixel[4]] > cb) {
                                                if (p[pixel[5]] > cb) {
                                                    if (p[pixel[6]] > cb) {
                                                        if (p[pixel[7]] > cb) {
                                                            is_a_corner();
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else {
                                    is_not_a_corner();
                                }
                            } else {
                                is_not_a_corner();
                            }
                        } else {
                            is_not_a_corner();
                        }
                    } else if (p[pixel[8]] < c_b) {
                        if (p[pixel[6]] < c_b) {
                            if (p[pixel[7]] < c_b) {
                                if (p[pixel[9]] < c_b) {
                                    if (p[pixel[10]] < c_b) {
                                        if (p[pixel[11]] < c_b) {
                                            if (p[pixel[12]] < c_b) {
                                                if (p[pixel[5]] < c_b) {
                                                    if (p[pixel[4]] < c_b) {
                                                        if (p[pixel[3]] < c_b) {
                                                            is_a_corner();
                                                        } else if (p[pixel[13]] < c_b) {
                                                            is_a_corner();
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else if (p[pixel[13]] < c_b) {
                                                        if (p[pixel[14]] < c_b) {
                                                            is_a_corner();
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else if (p[pixel[13]] < c_b) {
                                                    if (p[pixel[14]] < c_b) {
                                                        if (p[pixel[15]] < c_b) {
                                                            is_a_corner();
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else {
                                    is_not_a_corner();
                                }
                            } else {
                                is_not_a_corner();
                            }
                        } else {
                            is_not_a_corner();
                        }
                    } else {
                        is_not_a_corner();
                    }
                } else if (p[pixel[1]] < c_b) {
                    if (p[pixel[7]] > cb) {
                        if (p[pixel[8]] > cb) {
                            if (p[pixel[9]] > cb) {
                                if (p[pixel[10]] > cb) {
                                    if (p[pixel[11]] > cb) {
                                        if (p[pixel[12]] > cb) {
                                            if (p[pixel[13]] > cb) {
                                                if (p[pixel[14]] > cb) {
                                                    if (p[pixel[15]] > cb) {
                                                        is_a_corner();
                                                    } else if (p[pixel[5]] > cb) {
                                                        if (p[pixel[6]] > cb) {
                                                            is_a_corner();
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else if (p[pixel[4]] > cb) {
                                                    if (p[pixel[5]] > cb) {
                                                        if (p[pixel[6]] > cb) {
                                                            is_a_corner();
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else if (p[pixel[3]] > cb) {
                                                if (p[pixel[4]] > cb) {
                                                    if (p[pixel[5]] > cb) {
                                                        if (p[pixel[6]] > cb) {
                                                            is_a_corner();
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else if (p[pixel[2]] > cb) {
                                            if (p[pixel[3]] > cb) {
                                                if (p[pixel[4]] > cb) {
                                                    if (p[pixel[5]] > cb) {
                                                        if (p[pixel[6]] > cb) {
                                                            is_a_corner();
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else {
                                    is_not_a_corner();
                                }
                            } else {
                                is_not_a_corner();
                            }
                        } else {
                            is_not_a_corner();
                        }
                    } else if (p[pixel[7]] < c_b) {
                        if (p[pixel[6]] < c_b) {
                            if (p[pixel[8]] < c_b) {
                                if (p[pixel[9]] < c_b) {
                                    if (p[pixel[10]] < c_b) {
                                        if (p[pixel[5]] < c_b) {
                                            if (p[pixel[4]] < c_b) {
                                                if (p[pixel[3]] < c_b) {
                                                    if (p[pixel[2]] < c_b) {
                                                        is_a_corner();
                                                    } else if (p[pixel[11]] < c_b) {
                                                        if (p[pixel[12]] < c_b) {
                                                            is_a_corner();
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else if (p[pixel[11]] < c_b) {
                                                    if (p[pixel[12]] < c_b) {
                                                        if (p[pixel[13]] < c_b) {
                                                            is_a_corner();
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else if (p[pixel[11]] < c_b) {
                                                if (p[pixel[12]] < c_b) {
                                                    if (p[pixel[13]] < c_b) {
                                                        if (p[pixel[14]] < c_b) {
                                                            is_a_corner();
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else if (p[pixel[11]] < c_b) {
                                            if (p[pixel[12]] < c_b) {
                                                if (p[pixel[13]] < c_b) {
                                                    if (p[pixel[14]] < c_b) {
                                                        if (p[pixel[15]] < c_b) {
                                                            is_a_corner();
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else {
                                    is_not_a_corner();
                                }
                            } else {
                                is_not_a_corner();
                            }
                        } else {
                            is_not_a_corner();
                        }
                    } else {
                        is_not_a_corner();
                    }
                } else if (p[pixel[7]] > cb) {
                    if (p[pixel[8]] > cb) {
                        if (p[pixel[9]] > cb) {
                            if (p[pixel[10]] > cb) {
                                if (p[pixel[11]] > cb) {
                                    if (p[pixel[12]] > cb) {
                                        if (p[pixel[13]] > cb) {
                                            if (p[pixel[14]] > cb) {
                                                if (p[pixel[15]] > cb) {
                                                    is_a_corner();
                                                } else if (p[pixel[5]] > cb) {
                                                    if (p[pixel[6]] > cb) {
                                                        is_a_corner();
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else if (p[pixel[4]] > cb) {
                                                if (p[pixel[5]] > cb) {
                                                    if (p[pixel[6]] > cb) {
                                                        is_a_corner();
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else if (p[pixel[3]] > cb) {
                                            if (p[pixel[4]] > cb) {
                                                if (p[pixel[5]] > cb) {
                                                    if (p[pixel[6]] > cb) {
                                                        is_a_corner();
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else if (p[pixel[2]] > cb) {
                                        if (p[pixel[3]] > cb) {
                                            if (p[pixel[4]] > cb) {
                                                if (p[pixel[5]] > cb) {
                                                    if (p[pixel[6]] > cb) {
                                                        is_a_corner();
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else {
                                    is_not_a_corner();
                                }
                            } else {
                                is_not_a_corner();
                            }
                        } else {
                            is_not_a_corner();
                        }
                    } else {
                        is_not_a_corner();
                    }
                } else if (p[pixel[7]] < c_b) {
                    if (p[pixel[6]] < c_b) {
                        if (p[pixel[8]] < c_b) {
                            if (p[pixel[9]] < c_b) {
                                if (p[pixel[10]] < c_b) {
                                    if (p[pixel[11]] < c_b) {
                                        if (p[pixel[5]] < c_b) {
                                            if (p[pixel[4]] < c_b) {
                                                if (p[pixel[3]] < c_b) {
                                                    if (p[pixel[2]] < c_b) {
                                                        is_a_corner();
                                                    } else if (p[pixel[12]] < c_b) {
                                                        is_a_corner();
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else if (p[pixel[12]] < c_b) {
                                                    if (p[pixel[13]] < c_b) {
                                                        is_a_corner();
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else if (p[pixel[12]] < c_b) {
                                                if (p[pixel[13]] < c_b) {
                                                    if (p[pixel[14]] < c_b) {
                                                        is_a_corner();
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else if (p[pixel[12]] < c_b) {
                                            if (p[pixel[13]] < c_b) {
                                                if (p[pixel[14]] < c_b) {
                                                    if (p[pixel[15]] < c_b) {
                                                        is_a_corner();
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else {
                                    is_not_a_corner();
                                }
                            } else {
                                is_not_a_corner();
                            }
                        } else {
                            is_not_a_corner();
                        }
                    } else {
                        is_not_a_corner();
                    }
                } else {
                    is_not_a_corner();
                }
            } else if (p[pixel[0]] < c_b) {
                if (p[pixel[1]] > cb) {
                    if (p[pixel[7]] > cb) {
                        if (p[pixel[6]] > cb) {
                            if (p[pixel[8]] > cb) {
                                if (p[pixel[9]] > cb) {
                                    if (p[pixel[10]] > cb) {
                                        if (p[pixel[5]] > cb) {
                                            if (p[pixel[4]] > cb) {
                                                if (p[pixel[3]] > cb) {
                                                    if (p[pixel[2]] > cb) {
                                                        is_a_corner();
                                                    } else if (p[pixel[11]] > cb) {
                                                        if (p[pixel[12]] > cb) {
                                                            is_a_corner();
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else if (p[pixel[11]] > cb) {
                                                    if (p[pixel[12]] > cb) {
                                                        if (p[pixel[13]] > cb) {
                                                            is_a_corner();
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else if (p[pixel[11]] > cb) {
                                                if (p[pixel[12]] > cb) {
                                                    if (p[pixel[13]] > cb) {
                                                        if (p[pixel[14]] > cb) {
                                                            is_a_corner();
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else if (p[pixel[11]] > cb) {
                                            if (p[pixel[12]] > cb) {
                                                if (p[pixel[13]] > cb) {
                                                    if (p[pixel[14]] > cb) {
                                                        if (p[pixel[15]] > cb) {
                                                            is_a_corner();
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else {
                                    is_not_a_corner();
                                }
                            } else {
                                is_not_a_corner();
                            }
                        } else {
                            is_not_a_corner();
                        }
                    } else if (p[pixel[7]] < c_b) {
                        if (p[pixel[8]] < c_b) {
                            if (p[pixel[9]] < c_b) {
                                if (p[pixel[10]] < c_b) {
                                    if (p[pixel[11]] < c_b) {
                                        if (p[pixel[12]] < c_b) {
                                            if (p[pixel[13]] < c_b) {
                                                if (p[pixel[14]] < c_b) {
                                                    if (p[pixel[15]] < c_b) {
                                                        is_a_corner();
                                                    } else if (p[pixel[5]] < c_b) {
                                                        if (p[pixel[6]] < c_b) {
                                                            is_a_corner();
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else if (p[pixel[4]] < c_b) {
                                                    if (p[pixel[5]] < c_b) {
                                                        if (p[pixel[6]] < c_b) {
                                                            is_a_corner();
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else if (p[pixel[3]] < c_b) {
                                                if (p[pixel[4]] < c_b) {
                                                    if (p[pixel[5]] < c_b) {
                                                        if (p[pixel[6]] < c_b) {
                                                            is_a_corner();
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else if (p[pixel[2]] < c_b) {
                                            if (p[pixel[3]] < c_b) {
                                                if (p[pixel[4]] < c_b) {
                                                    if (p[pixel[5]] < c_b) {
                                                        if (p[pixel[6]] < c_b) {
                                                            is_a_corner();
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else {
                                    is_not_a_corner();
                                }
                            } else {
                                is_not_a_corner();
                            }
                        } else {
                            is_not_a_corner();
                        }
                    } else {
                        is_not_a_corner();
                    }
                } else if (p[pixel[1]] < c_b) {
                    if (p[pixel[2]] > cb) {
                        if (p[pixel[8]] > cb) {
                            if (p[pixel[6]] > cb) {
                                if (p[pixel[7]] > cb) {
                                    if (p[pixel[9]] > cb) {
                                        if (p[pixel[10]] > cb) {
                                            if (p[pixel[11]] > cb) {
                                                if (p[pixel[5]] > cb) {
                                                    if (p[pixel[4]] > cb) {
                                                        if (p[pixel[3]] > cb) {
                                                            is_a_corner();
                                                        } else if (p[pixel[12]] > cb) {
                                                            if (p[pixel[13]] > cb) {
                                                                is_a_corner();
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else if (p[pixel[12]] > cb) {
                                                        if (p[pixel[13]] > cb) {
                                                            if (p[pixel[14]] > cb) {
                                                                is_a_corner();
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else if (p[pixel[12]] > cb) {
                                                    if (p[pixel[13]] > cb) {
                                                        if (p[pixel[14]] > cb) {
                                                            if (p[pixel[15]] > cb) {
                                                                is_a_corner();
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else {
                                    is_not_a_corner();
                                }
                            } else {
                                is_not_a_corner();
                            }
                        } else if (p[pixel[8]] < c_b) {
                            if (p[pixel[9]] < c_b) {
                                if (p[pixel[10]] < c_b) {
                                    if (p[pixel[11]] < c_b) {
                                        if (p[pixel[12]] < c_b) {
                                            if (p[pixel[13]] < c_b) {
                                                if (p[pixel[14]] < c_b) {
                                                    if (p[pixel[15]] < c_b) {
                                                        is_a_corner();
                                                    } else if (p[pixel[5]] < c_b) {
                                                        if (p[pixel[6]] < c_b) {
                                                            if (p[pixel[7]] < c_b) {
                                                                is_a_corner();
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else if (p[pixel[4]] < c_b) {
                                                    if (p[pixel[5]] < c_b) {
                                                        if (p[pixel[6]] < c_b) {
                                                            if (p[pixel[7]] < c_b) {
                                                                is_a_corner();
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else if (p[pixel[3]] < c_b) {
                                                if (p[pixel[4]] < c_b) {
                                                    if (p[pixel[5]] < c_b) {
                                                        if (p[pixel[6]] < c_b) {
                                                            if (p[pixel[7]] < c_b) {
                                                                is_a_corner();
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else {
                                    is_not_a_corner();
                                }
                            } else {
                                is_not_a_corner();
                            }
                        } else {
                            is_not_a_corner();
                        }
                    } else if (p[pixel[2]] < c_b) {
                        if (p[pixel[3]] > cb) {
                            if (p[pixel[9]] > cb) {
                                if (p[pixel[6]] > cb) {
                                    if (p[pixel[7]] > cb) {
                                        if (p[pixel[8]] > cb) {
                                            if (p[pixel[10]] > cb) {
                                                if (p[pixel[11]] > cb) {
                                                    if (p[pixel[12]] > cb) {
                                                        if (p[pixel[5]] > cb) {
                                                            if (p[pixel[4]] > cb) {
                                                                is_a_corner();
                                                            } else if (p[pixel[13]] > cb) {
                                                                if (p[pixel[14]] > cb) {
                                                                    is_a_corner();
                                                                } else {
                                                                    is_not_a_corner();
                                                                }
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else if (p[pixel[13]] > cb) {
                                                            if (p[pixel[14]] > cb) {
                                                                if (p[pixel[15]] > cb) {
                                                                    is_a_corner();
                                                                } else {
                                                                    is_not_a_corner();
                                                                }
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else {
                                    is_not_a_corner();
                                }
                            } else if (p[pixel[9]] < c_b) {
                                if (p[pixel[10]] < c_b) {
                                    if (p[pixel[11]] < c_b) {
                                        if (p[pixel[12]] < c_b) {
                                            if (p[pixel[13]] < c_b) {
                                                if (p[pixel[14]] < c_b) {
                                                    if (p[pixel[15]] < c_b) {
                                                        is_a_corner();
                                                    } else if (p[pixel[5]] < c_b) {
                                                        if (p[pixel[6]] < c_b) {
                                                            if (p[pixel[7]] < c_b) {
                                                                if (p[pixel[8]] < c_b) {
                                                                    is_a_corner();
                                                                } else {
                                                                    is_not_a_corner();
                                                                }
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else if (p[pixel[4]] < c_b) {
                                                    if (p[pixel[5]] < c_b) {
                                                        if (p[pixel[6]] < c_b) {
                                                            if (p[pixel[7]] < c_b) {
                                                                if (p[pixel[8]] < c_b) {
                                                                    is_a_corner();
                                                                } else {
                                                                    is_not_a_corner();
                                                                }
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else {
                                    is_not_a_corner();
                                }
                            } else {
                                is_not_a_corner();
                            }
                        } else if (p[pixel[3]] < c_b) {
                            if (p[pixel[4]] > cb) {
                                if (p[pixel[14]] > cb) {
                                    if (p[pixel[6]] > cb) {
                                        if (p[pixel[7]] > cb) {
                                            if (p[pixel[8]] > cb) {
                                                if (p[pixel[9]] > cb) {
                                                    if (p[pixel[10]] > cb) {
                                                        if (p[pixel[11]] > cb) {
                                                            if (p[pixel[12]] > cb) {
                                                                if (p[pixel[13]] > cb) {
                                                                    if (p[pixel[5]] > cb) {
                                                                        is_a_corner();
                                                                    } else if (p[pixel[15]] > cb) {
                                                                        is_a_corner();
                                                                    } else {
                                                                        is_not_a_corner();
                                                                    }
                                                                } else {
                                                                    is_not_a_corner();
                                                                }
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else if (p[pixel[14]] < c_b) {
                                    if (p[pixel[10]] > cb) {
                                        if (p[pixel[5]] > cb) {
                                            if (p[pixel[6]] > cb) {
                                                if (p[pixel[7]] > cb) {
                                                    if (p[pixel[8]] > cb) {
                                                        if (p[pixel[9]] > cb) {
                                                            if (p[pixel[11]] > cb) {
                                                                if (p[pixel[12]] > cb) {
                                                                    if (p[pixel[13]] > cb) {
                                                                        is_a_corner();
                                                                    } else {
                                                                        is_not_a_corner();
                                                                    }
                                                                } else {
                                                                    is_not_a_corner();
                                                                }
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else if (p[pixel[10]] < c_b) {
                                        if (p[pixel[11]] < c_b) {
                                            if (p[pixel[12]] < c_b) {
                                                if (p[pixel[13]] < c_b) {
                                                    if (p[pixel[15]] < c_b) {
                                                        is_a_corner();
                                                    } else if (p[pixel[5]] < c_b) {
                                                        if (p[pixel[6]] < c_b) {
                                                            if (p[pixel[7]] < c_b) {
                                                                if (p[pixel[8]] < c_b) {
                                                                    if (p[pixel[9]] < c_b) {
                                                                        is_a_corner();
                                                                    } else {
                                                                        is_not_a_corner();
                                                                    }
                                                                } else {
                                                                    is_not_a_corner();
                                                                }
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else if (p[pixel[5]] > cb) {
                                    if (p[pixel[6]] > cb) {
                                        if (p[pixel[7]] > cb) {
                                            if (p[pixel[8]] > cb) {
                                                if (p[pixel[9]] > cb) {
                                                    if (p[pixel[10]] > cb) {
                                                        if (p[pixel[11]] > cb) {
                                                            if (p[pixel[12]] > cb) {
                                                                if (p[pixel[13]] > cb) {
                                                                    is_a_corner();
                                                                } else {
                                                                    is_not_a_corner();
                                                                }
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else {
                                    is_not_a_corner();
                                }
                            } else if (p[pixel[4]] < c_b) {
                                if (p[pixel[5]] > cb) {
                                    if (p[pixel[15]] < c_b) {
                                        if (p[pixel[11]] > cb) {
                                            if (p[pixel[6]] > cb) {
                                                if (p[pixel[7]] > cb) {
                                                    if (p[pixel[8]] > cb) {
                                                        if (p[pixel[9]] > cb) {
                                                            if (p[pixel[10]] > cb) {
                                                                if (p[pixel[12]] > cb) {
                                                                    if (p[pixel[13]] > cb) {
                                                                        if (p[pixel[14]] > cb) {
                                                                            is_a_corner();
                                                                        } else {
                                                                            is_not_a_corner();
                                                                        }
                                                                    } else {
                                                                        is_not_a_corner();
                                                                    }
                                                                } else {
                                                                    is_not_a_corner();
                                                                }
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else if (p[pixel[11]] < c_b) {
                                            if (p[pixel[12]] < c_b) {
                                                if (p[pixel[13]] < c_b) {
                                                    if (p[pixel[14]] < c_b) {
                                                        is_a_corner();
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else if (p[pixel[6]] > cb) {
                                        if (p[pixel[7]] > cb) {
                                            if (p[pixel[8]] > cb) {
                                                if (p[pixel[9]] > cb) {
                                                    if (p[pixel[10]] > cb) {
                                                        if (p[pixel[11]] > cb) {
                                                            if (p[pixel[12]] > cb) {
                                                                if (p[pixel[13]] > cb) {
                                                                    if (p[pixel[14]] > cb) {
                                                                        is_a_corner();
                                                                    } else {
                                                                        is_not_a_corner();
                                                                    }
                                                                } else {
                                                                    is_not_a_corner();
                                                                }
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else if (p[pixel[5]] < c_b) {
                                    if (p[pixel[6]] > cb) {
                                        if (p[pixel[12]] > cb) {
                                            if (p[pixel[7]] > cb) {
                                                if (p[pixel[8]] > cb) {
                                                    if (p[pixel[9]] > cb) {
                                                        if (p[pixel[10]] > cb) {
                                                            if (p[pixel[11]] > cb) {
                                                                if (p[pixel[13]] > cb) {
                                                                    if (p[pixel[14]] > cb) {
                                                                        if (p[pixel[15]] > cb) {
                                                                            is_a_corner();
                                                                        } else {
                                                                            is_not_a_corner();
                                                                        }
                                                                    } else {
                                                                        is_not_a_corner();
                                                                    }
                                                                } else {
                                                                    is_not_a_corner();
                                                                }
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else if (p[pixel[12]] < c_b) {
                                            if (p[pixel[13]] < c_b) {
                                                if (p[pixel[14]] < c_b) {
                                                    if (p[pixel[15]] < c_b) {
                                                        is_a_corner();
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else if (p[pixel[6]] < c_b) {
                                        if (p[pixel[7]] < c_b) {
                                            if (p[pixel[8]] < c_b) {
                                                if (p[pixel[9]] < c_b) {
                                                    is_a_corner();
                                                } else if (p[pixel[15]] < c_b) {
                                                    is_a_corner();
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else if (p[pixel[14]] < c_b) {
                                                if (p[pixel[15]] < c_b) {
                                                    is_a_corner();
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else if (p[pixel[13]] < c_b) {
                                            if (p[pixel[14]] < c_b) {
                                                if (p[pixel[15]] < c_b) {
                                                    is_a_corner();
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else if (p[pixel[12]] < c_b) {
                                        if (p[pixel[13]] < c_b) {
                                            if (p[pixel[14]] < c_b) {
                                                if (p[pixel[15]] < c_b) {
                                                    is_a_corner();
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else if (p[pixel[11]] > cb) {
                                    if (p[pixel[6]] > cb) {
                                        if (p[pixel[7]] > cb) {
                                            if (p[pixel[8]] > cb) {
                                                if (p[pixel[9]] > cb) {
                                                    if (p[pixel[10]] > cb) {
                                                        if (p[pixel[12]] > cb) {
                                                            if (p[pixel[13]] > cb) {
                                                                if (p[pixel[14]] > cb) {
                                                                    if (p[pixel[15]] > cb) {
                                                                        is_a_corner();
                                                                    } else {
                                                                        is_not_a_corner();
                                                                    }
                                                                } else {
                                                                    is_not_a_corner();
                                                                }
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else if (p[pixel[11]] < c_b) {
                                    if (p[pixel[12]] < c_b) {
                                        if (p[pixel[13]] < c_b) {
                                            if (p[pixel[14]] < c_b) {
                                                if (p[pixel[15]] < c_b) {
                                                    is_a_corner();
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else {
                                    is_not_a_corner();
                                }
                            } else if (p[pixel[10]] > cb) {
                                if (p[pixel[6]] > cb) {
                                    if (p[pixel[7]] > cb) {
                                        if (p[pixel[8]] > cb) {
                                            if (p[pixel[9]] > cb) {
                                                if (p[pixel[11]] > cb) {
                                                    if (p[pixel[12]] > cb) {
                                                        if (p[pixel[13]] > cb) {
                                                            if (p[pixel[14]] > cb) {
                                                                if (p[pixel[5]] > cb) {
                                                                    is_a_corner();
                                                                } else if (p[pixel[15]] > cb) {
                                                                    is_a_corner();
                                                                } else {
                                                                    is_not_a_corner();
                                                                }
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else {
                                    is_not_a_corner();
                                }
                            } else if (p[pixel[10]] < c_b) {
                                if (p[pixel[11]] < c_b) {
                                    if (p[pixel[12]] < c_b) {
                                        if (p[pixel[13]] < c_b) {
                                            if (p[pixel[14]] < c_b) {
                                                if (p[pixel[15]] < c_b) {
                                                    is_a_corner();
                                                } else if (p[pixel[5]] < c_b) {
                                                    if (p[pixel[6]] < c_b) {
                                                        if (p[pixel[7]] < c_b) {
                                                            if (p[pixel[8]] < c_b) {
                                                                if (p[pixel[9]] < c_b) {
                                                                    is_a_corner();
                                                                } else {
                                                                    is_not_a_corner();
                                                                }
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else {
                                    is_not_a_corner();
                                }
                            } else {
                                is_not_a_corner();
                            }
                        } else if (p[pixel[9]] > cb) {
                            if (p[pixel[6]] > cb) {
                                if (p[pixel[7]] > cb) {
                                    if (p[pixel[8]] > cb) {
                                        if (p[pixel[10]] > cb) {
                                            if (p[pixel[11]] > cb) {
                                                if (p[pixel[12]] > cb) {
                                                    if (p[pixel[13]] > cb) {
                                                        if (p[pixel[5]] > cb) {
                                                            if (p[pixel[4]] > cb) {
                                                                is_a_corner();
                                                            } else if (p[pixel[14]] > cb) {
                                                                is_a_corner();
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else if (p[pixel[14]] > cb) {
                                                            if (p[pixel[15]] > cb) {
                                                                is_a_corner();
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else {
                                    is_not_a_corner();
                                }
                            } else {
                                is_not_a_corner();
                            }
                        } else if (p[pixel[9]] < c_b) {
                            if (p[pixel[10]] < c_b) {
                                if (p[pixel[11]] < c_b) {
                                    if (p[pixel[12]] < c_b) {
                                        if (p[pixel[13]] < c_b) {
                                            if (p[pixel[14]] < c_b) {
                                                if (p[pixel[15]] < c_b) {
                                                    is_a_corner();
                                                } else if (p[pixel[5]] < c_b) {
                                                    if (p[pixel[6]] < c_b) {
                                                        if (p[pixel[7]] < c_b) {
                                                            if (p[pixel[8]] < c_b) {
                                                                is_a_corner();
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else if (p[pixel[4]] < c_b) {
                                                if (p[pixel[5]] < c_b) {
                                                    if (p[pixel[6]] < c_b) {
                                                        if (p[pixel[7]] < c_b) {
                                                            if (p[pixel[8]] < c_b) {
                                                                is_a_corner();
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else {
                                    is_not_a_corner();
                                }
                            } else {
                                is_not_a_corner();
                            }
                        } else {
                            is_not_a_corner();
                        }
                    } else if (p[pixel[8]] > cb) {
                        if (p[pixel[6]] > cb) {
                            if (p[pixel[7]] > cb) {
                                if (p[pixel[9]] > cb) {
                                    if (p[pixel[10]] > cb) {
                                        if (p[pixel[11]] > cb) {
                                            if (p[pixel[12]] > cb) {
                                                if (p[pixel[5]] > cb) {
                                                    if (p[pixel[4]] > cb) {
                                                        if (p[pixel[3]] > cb) {
                                                            is_a_corner();
                                                        } else if (p[pixel[13]] > cb) {
                                                            is_a_corner();
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else if (p[pixel[13]] > cb) {
                                                        if (p[pixel[14]] > cb) {
                                                            is_a_corner();
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else if (p[pixel[13]] > cb) {
                                                    if (p[pixel[14]] > cb) {
                                                        if (p[pixel[15]] > cb) {
                                                            is_a_corner();
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else {
                                    is_not_a_corner();
                                }
                            } else {
                                is_not_a_corner();
                            }
                        } else {
                            is_not_a_corner();
                        }
                    } else if (p[pixel[8]] < c_b) {
                        if (p[pixel[9]] < c_b) {
                            if (p[pixel[10]] < c_b) {
                                if (p[pixel[11]] < c_b) {
                                    if (p[pixel[12]] < c_b) {
                                        if (p[pixel[13]] < c_b) {
                                            if (p[pixel[14]] < c_b) {
                                                if (p[pixel[15]] < c_b) {
                                                    is_a_corner();
                                                } else if (p[pixel[5]] < c_b) {
                                                    if (p[pixel[6]] < c_b) {
                                                        if (p[pixel[7]] < c_b) {
                                                            is_a_corner();
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else if (p[pixel[4]] < c_b) {
                                                if (p[pixel[5]] < c_b) {
                                                    if (p[pixel[6]] < c_b) {
                                                        if (p[pixel[7]] < c_b) {
                                                            is_a_corner();
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else if (p[pixel[3]] < c_b) {
                                            if (p[pixel[4]] < c_b) {
                                                if (p[pixel[5]] < c_b) {
                                                    if (p[pixel[6]] < c_b) {
                                                        if (p[pixel[7]] < c_b) {
                                                            is_a_corner();
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else {
                                    is_not_a_corner();
                                }
                            } else {
                                is_not_a_corner();
                            }
                        } else {
                            is_not_a_corner();
                        }
                    } else {
                        is_not_a_corner();
                    }
                } else if (p[pixel[7]] > cb) {
                    if (p[pixel[6]] > cb) {
                        if (p[pixel[8]] > cb) {
                            if (p[pixel[9]] > cb) {
                                if (p[pixel[10]] > cb) {
                                    if (p[pixel[11]] > cb) {
                                        if (p[pixel[5]] > cb) {
                                            if (p[pixel[4]] > cb) {
                                                if (p[pixel[3]] > cb) {
                                                    if (p[pixel[2]] > cb) {
                                                        is_a_corner();
                                                    } else if (p[pixel[12]] > cb) {
                                                        is_a_corner();
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else if (p[pixel[12]] > cb) {
                                                    if (p[pixel[13]] > cb) {
                                                        is_a_corner();
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else if (p[pixel[12]] > cb) {
                                                if (p[pixel[13]] > cb) {
                                                    if (p[pixel[14]] > cb) {
                                                        is_a_corner();
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else if (p[pixel[12]] > cb) {
                                            if (p[pixel[13]] > cb) {
                                                if (p[pixel[14]] > cb) {
                                                    if (p[pixel[15]] > cb) {
                                                        is_a_corner();
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else {
                                    is_not_a_corner();
                                }
                            } else {
                                is_not_a_corner();
                            }
                        } else {
                            is_not_a_corner();
                        }
                    } else {
                        is_not_a_corner();
                    }
                } else if (p[pixel[7]] < c_b) {
                    if (p[pixel[8]] < c_b) {
                        if (p[pixel[9]] < c_b) {
                            if (p[pixel[10]] < c_b) {
                                if (p[pixel[11]] < c_b) {
                                    if (p[pixel[12]] < c_b) {
                                        if (p[pixel[13]] < c_b) {
                                            if (p[pixel[14]] < c_b) {
                                                if (p[pixel[15]] < c_b) {
                                                    is_a_corner();
                                                } else if (p[pixel[5]] < c_b) {
                                                    if (p[pixel[6]] < c_b) {
                                                        is_a_corner();
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else if (p[pixel[4]] < c_b) {
                                                if (p[pixel[5]] < c_b) {
                                                    if (p[pixel[6]] < c_b) {
                                                        is_a_corner();
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else if (p[pixel[3]] < c_b) {
                                            if (p[pixel[4]] < c_b) {
                                                if (p[pixel[5]] < c_b) {
                                                    if (p[pixel[6]] < c_b) {
                                                        is_a_corner();
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else if (p[pixel[2]] < c_b) {
                                        if (p[pixel[3]] < c_b) {
                                            if (p[pixel[4]] < c_b) {
                                                if (p[pixel[5]] < c_b) {
                                                    if (p[pixel[6]] < c_b) {
                                                        is_a_corner();
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else {
                                    is_not_a_corner();
                                }
                            } else {
                                is_not_a_corner();
                            }
                        } else {
                            is_not_a_corner();
                        }
                    } else {
                        is_not_a_corner();
                    }
                } else {
                    is_not_a_corner();
                }
            } else if (p[pixel[6]] > cb) {
                if (p[pixel[7]] > cb) {
                    if (p[pixel[8]] > cb) {
                        if (p[pixel[9]] > cb) {
                            if (p[pixel[10]] > cb) {
                                if (p[pixel[5]] > cb) {
                                    if (p[pixel[4]] > cb) {
                                        if (p[pixel[3]] > cb) {
                                            if (p[pixel[2]] > cb) {
                                                if (p[pixel[1]] > cb) {
                                                    is_a_corner();
                                                } else if (p[pixel[11]] > cb) {
                                                    is_a_corner();
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else if (p[pixel[11]] > cb) {
                                                if (p[pixel[12]] > cb) {
                                                    is_a_corner();
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else if (p[pixel[11]] > cb) {
                                            if (p[pixel[12]] > cb) {
                                                if (p[pixel[13]] > cb) {
                                                    is_a_corner();
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else if (p[pixel[11]] > cb) {
                                        if (p[pixel[12]] > cb) {
                                            if (p[pixel[13]] > cb) {
                                                if (p[pixel[14]] > cb) {
                                                    is_a_corner();
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else if (p[pixel[11]] > cb) {
                                    if (p[pixel[12]] > cb) {
                                        if (p[pixel[13]] > cb) {
                                            if (p[pixel[14]] > cb) {
                                                if (p[pixel[15]] > cb) {
                                                    is_a_corner();
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else {
                                    is_not_a_corner();
                                }
                            } else {
                                is_not_a_corner();
                            }
                        } else {
                            is_not_a_corner();
                        }
                    } else {
                        is_not_a_corner();
                    }
                } else {
                    is_not_a_corner();
                }
            } else if (p[pixel[6]] < c_b) {
                if (p[pixel[7]] < c_b) {
                    if (p[pixel[8]] < c_b) {
                        if (p[pixel[9]] < c_b) {
                            if (p[pixel[10]] < c_b) {
                                if (p[pixel[5]] < c_b) {
                                    if (p[pixel[4]] < c_b) {
                                        if (p[pixel[3]] < c_b) {
                                            if (p[pixel[2]] < c_b) {
                                                if (p[pixel[1]] < c_b) {
                                                    is_a_corner();
                                                } else if (p[pixel[11]] < c_b) {
                                                    is_a_corner();
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else if (p[pixel[11]] < c_b) {
                                                if (p[pixel[12]] < c_b) {
                                                    is_a_corner();
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else if (p[pixel[11]] < c_b) {
                                            if (p[pixel[12]] < c_b) {
                                                if (p[pixel[13]] < c_b) {
                                                    is_a_corner();
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else if (p[pixel[11]] < c_b) {
                                        if (p[pixel[12]] < c_b) {
                                            if (p[pixel[13]] < c_b) {
                                                if (p[pixel[14]] < c_b) {
                                                    is_a_corner();
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else if (p[pixel[11]] < c_b) {
                                    if (p[pixel[12]] < c_b) {
                                        if (p[pixel[13]] < c_b) {
                                            if (p[pixel[14]] < c_b) {
                                                if (p[pixel[15]] < c_b) {
                                                    is_a_corner();
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
                                } else {
                                    is_not_a_corner();
                                }
                            } else {
                                is_not_a_corner();
                            }
                        } else {
                            is_not_a_corner();
                        }
                    } else {
                        is_not_a_corner();
                    }
                } else {
                    is_not_a_corner();
                }
            } else {
                is_not_a_corner();
            }
        }
        return bmin;
    }
}
