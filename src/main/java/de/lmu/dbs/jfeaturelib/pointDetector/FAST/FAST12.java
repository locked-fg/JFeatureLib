/*
 *Copyright (c) 2006, 2008, 2009, 2010 Edward Rosten
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 	*Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 * 
 *      *Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 * 
 * 	*Neither the name of the University of Cambridge nor the names of 
 *       its contributors may be used to endorse or promote products derived 
 *       from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.lmu.dbs.jfeaturelib.pointDetector.FAST;

import de.lmu.dbs.jfeaturelib.ImagePoint;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Robert Zelhofer
 */
public class FAST12 {

    int bmin;
    int b;
    int bmax;
    boolean endScore;

    public List<ImagePoint> fast12_detect_nonmax(int[] im, int xsize, int ysize, int stride, int b, List<ImagePoint> ret_num_corners) {
        List<ImagePoint> corners;
        int num_corners = 0;
        int[] scores;
        List<ImagePoint> nonmax = new ArrayList<>();

        corners = fast12_detect(im, xsize, ysize, stride, b, num_corners);
        scores = fast12_score(im, stride, corners, corners.size(), b);
        FASTNonMaxSuppression nonmaxSuppressor = new FASTNonMaxSuppression();
        nonmax = nonmaxSuppressor.nonmax_suppression(corners, scores, corners.size());

        return nonmax;
    }

    private List<ImagePoint> fast12_detect(int[] im, int xsize, int ysize, int stride, int b, int ret_num_corners) {
        int num_corners = 0;
        List<ImagePoint> ret_corners;
        int rsize = 512;
        int[] pixel = new int[16];
        int x, y;

        //ret_corners = (xy *) malloc(sizeof(xy) * rsize);
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
                                                        if (p[pixel[10]] > cb) {
                                                            if (p[pixel[11]] > cb) {
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
                                        } else if (p[pixel[10]] > cb) {
                                            if (p[pixel[11]] > cb) {
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
                                    } else if (p[pixel[9]] > cb) {
                                        if (p[pixel[10]] > cb) {
                                            if (p[pixel[11]] > cb) {
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
                                } else if (p[pixel[4]] < c_b) {
                                    if (p[pixel[8]] > cb) {
                                        if (p[pixel[9]] > cb) {
                                            if (p[pixel[10]] > cb) {
                                                if (p[pixel[11]] > cb) {
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
                                    } else if (p[pixel[8]] < c_b) {
                                        if (p[pixel[5]] < c_b) {
                                            if (p[pixel[6]] < c_b) {
                                                if (p[pixel[7]] < c_b) {
                                                    if (p[pixel[9]] < c_b) {
                                                        if (p[pixel[10]] < c_b) {
                                                            if (p[pixel[11]] < c_b) {
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
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
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
                                if (p[pixel[15]] > cb) {
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
                                    } else if (p[pixel[7]] < c_b) {
                                        if (p[pixel[4]] < c_b) {
                                            if (p[pixel[5]] < c_b) {
                                                if (p[pixel[6]] < c_b) {
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
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
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
                                if (p[pixel[4]] < c_b) {
                                    if (p[pixel[5]] < c_b) {
                                        if (p[pixel[6]] < c_b) {
                                            if (p[pixel[8]] < c_b) {
                                                if (p[pixel[9]] < c_b) {
                                                    if (p[pixel[10]] < c_b) {
                                                        if (p[pixel[11]] < c_b) {
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
                                } else {
                                    continue;
                                }
                            } else {
                                continue;
                            }
                        } else if (p[pixel[2]] < c_b) {
                            if (p[pixel[6]] > cb) {
                                if (p[pixel[7]] > cb) {
                                    if (p[pixel[8]] > cb) {
                                        if (p[pixel[9]] > cb) {
                                            if (p[pixel[10]] > cb) {
                                                if (p[pixel[11]] > cb) {
                                                    if (p[pixel[12]] > cb) {
                                                        if (p[pixel[13]] > cb) {
                                                            if (p[pixel[14]] > cb) {
                                                                if (p[pixel[15]] > cb) {
                                                                } else if (p[pixel[3]] > cb) {
                                                                    if (p[pixel[4]] > cb) {
                                                                        if (p[pixel[5]] > cb) {
                                                                        } else {
                                                                            continue;
                                                                        }
                                                                    } else {
                                                                        continue;
                                                                    }
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
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
                                if (p[pixel[4]] < c_b) {
                                    if (p[pixel[5]] < c_b) {
                                        if (p[pixel[7]] < c_b) {
                                            if (p[pixel[8]] < c_b) {
                                                if (p[pixel[9]] < c_b) {
                                                    if (p[pixel[10]] < c_b) {
                                                        if (p[pixel[11]] < c_b) {
                                                            if (p[pixel[12]] < c_b) {
                                                                if (p[pixel[13]] < c_b) {
                                                                    if (p[pixel[3]] < c_b) {
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
                                                            if (p[pixel[15]] > cb) {
                                                            } else if (p[pixel[3]] > cb) {
                                                                if (p[pixel[4]] > cb) {
                                                                    if (p[pixel[5]] > cb) {
                                                                    } else {
                                                                        continue;
                                                                    }
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
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
                            if (p[pixel[4]] < c_b) {
                                if (p[pixel[5]] < c_b) {
                                    if (p[pixel[7]] < c_b) {
                                        if (p[pixel[8]] < c_b) {
                                            if (p[pixel[9]] < c_b) {
                                                if (p[pixel[10]] < c_b) {
                                                    if (p[pixel[11]] < c_b) {
                                                        if (p[pixel[12]] < c_b) {
                                                            if (p[pixel[13]] < c_b) {
                                                                if (p[pixel[14]] < c_b) {
                                                                    if (p[pixel[3]] < c_b) {
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
                            } else {
                                continue;
                            }
                        } else {
                            continue;
                        }
                    } else if (p[pixel[1]] < c_b) {
                        if (p[pixel[5]] > cb) {
                            if (p[pixel[6]] > cb) {
                                if (p[pixel[7]] > cb) {
                                    if (p[pixel[8]] > cb) {
                                        if (p[pixel[9]] > cb) {
                                            if (p[pixel[10]] > cb) {
                                                if (p[pixel[11]] > cb) {
                                                    if (p[pixel[12]] > cb) {
                                                        if (p[pixel[13]] > cb) {
                                                            if (p[pixel[14]] > cb) {
                                                                if (p[pixel[15]] > cb) {
                                                                } else if (p[pixel[3]] > cb) {
                                                                    if (p[pixel[4]] > cb) {
                                                                    } else {
                                                                        continue;
                                                                    }
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else if (p[pixel[2]] > cb) {
                                                                if (p[pixel[3]] > cb) {
                                                                    if (p[pixel[4]] > cb) {
                                                                    } else {
                                                                        continue;
                                                                    }
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
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
                            if (p[pixel[4]] < c_b) {
                                if (p[pixel[6]] < c_b) {
                                    if (p[pixel[7]] < c_b) {
                                        if (p[pixel[8]] < c_b) {
                                            if (p[pixel[9]] < c_b) {
                                                if (p[pixel[10]] < c_b) {
                                                    if (p[pixel[11]] < c_b) {
                                                        if (p[pixel[12]] < c_b) {
                                                            if (p[pixel[3]] < c_b) {
                                                                if (p[pixel[2]] < c_b) {
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
                                                        if (p[pixel[14]] > cb) {
                                                            if (p[pixel[15]] > cb) {
                                                            } else if (p[pixel[3]] > cb) {
                                                                if (p[pixel[4]] > cb) {
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else if (p[pixel[2]] > cb) {
                                                            if (p[pixel[3]] > cb) {
                                                                if (p[pixel[4]] > cb) {
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
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
                        if (p[pixel[4]] < c_b) {
                            if (p[pixel[6]] < c_b) {
                                if (p[pixel[7]] < c_b) {
                                    if (p[pixel[8]] < c_b) {
                                        if (p[pixel[9]] < c_b) {
                                            if (p[pixel[10]] < c_b) {
                                                if (p[pixel[11]] < c_b) {
                                                    if (p[pixel[12]] < c_b) {
                                                        if (p[pixel[13]] < c_b) {
                                                            if (p[pixel[3]] < c_b) {
                                                                if (p[pixel[2]] < c_b) {
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
                        } else {
                            continue;
                        }
                    } else {
                        continue;
                    }
                } else if (p[pixel[0]] < c_b) {
                    if (p[pixel[1]] > cb) {
                        if (p[pixel[5]] > cb) {
                            if (p[pixel[4]] > cb) {
                                if (p[pixel[6]] > cb) {
                                    if (p[pixel[7]] > cb) {
                                        if (p[pixel[8]] > cb) {
                                            if (p[pixel[9]] > cb) {
                                                if (p[pixel[10]] > cb) {
                                                    if (p[pixel[11]] > cb) {
                                                        if (p[pixel[12]] > cb) {
                                                            if (p[pixel[3]] > cb) {
                                                                if (p[pixel[2]] > cb) {
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
                                                            if (p[pixel[14]] < c_b) {
                                                                if (p[pixel[15]] < c_b) {
                                                                } else if (p[pixel[3]] < c_b) {
                                                                    if (p[pixel[4]] < c_b) {
                                                                    } else {
                                                                        continue;
                                                                    }
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else if (p[pixel[2]] < c_b) {
                                                                if (p[pixel[3]] < c_b) {
                                                                    if (p[pixel[4]] < c_b) {
                                                                    } else {
                                                                        continue;
                                                                    }
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
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
                            if (p[pixel[6]] > cb) {
                                if (p[pixel[4]] > cb) {
                                    if (p[pixel[5]] > cb) {
                                        if (p[pixel[7]] > cb) {
                                            if (p[pixel[8]] > cb) {
                                                if (p[pixel[9]] > cb) {
                                                    if (p[pixel[10]] > cb) {
                                                        if (p[pixel[11]] > cb) {
                                                            if (p[pixel[12]] > cb) {
                                                                if (p[pixel[13]] > cb) {
                                                                    if (p[pixel[3]] > cb) {
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
                                                                if (p[pixel[15]] < c_b) {
                                                                } else if (p[pixel[3]] < c_b) {
                                                                    if (p[pixel[4]] < c_b) {
                                                                        if (p[pixel[5]] < c_b) {
                                                                        } else {
                                                                            continue;
                                                                        }
                                                                    } else {
                                                                        continue;
                                                                    }
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
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
                                if (p[pixel[15]] < c_b) {
                                    if (p[pixel[7]] > cb) {
                                        if (p[pixel[4]] > cb) {
                                            if (p[pixel[5]] > cb) {
                                                if (p[pixel[6]] > cb) {
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
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
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
                                    } else {
                                        continue;
                                    }
                                } else {
                                    continue;
                                }
                            } else if (p[pixel[3]] < c_b) {
                                if (p[pixel[4]] > cb) {
                                    if (p[pixel[8]] > cb) {
                                        if (p[pixel[5]] > cb) {
                                            if (p[pixel[6]] > cb) {
                                                if (p[pixel[7]] > cb) {
                                                    if (p[pixel[9]] > cb) {
                                                        if (p[pixel[10]] > cb) {
                                                            if (p[pixel[11]] > cb) {
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
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
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
                                                    if (p[pixel[9]] < c_b) {
                                                        if (p[pixel[10]] < c_b) {
                                                            if (p[pixel[11]] < c_b) {
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
                                        } else if (p[pixel[10]] < c_b) {
                                            if (p[pixel[11]] < c_b) {
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
                                    } else if (p[pixel[9]] < c_b) {
                                        if (p[pixel[10]] < c_b) {
                                            if (p[pixel[11]] < c_b) {
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
                                } else if (p[pixel[8]] < c_b) {
                                    if (p[pixel[9]] < c_b) {
                                        if (p[pixel[10]] < c_b) {
                                            if (p[pixel[11]] < c_b) {
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
                            } else if (p[pixel[7]] > cb) {
                                if (p[pixel[4]] > cb) {
                                    if (p[pixel[5]] > cb) {
                                        if (p[pixel[6]] > cb) {
                                            if (p[pixel[8]] > cb) {
                                                if (p[pixel[9]] > cb) {
                                                    if (p[pixel[10]] > cb) {
                                                        if (p[pixel[11]] > cb) {
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
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
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
                            if (p[pixel[4]] > cb) {
                                if (p[pixel[5]] > cb) {
                                    if (p[pixel[7]] > cb) {
                                        if (p[pixel[8]] > cb) {
                                            if (p[pixel[9]] > cb) {
                                                if (p[pixel[10]] > cb) {
                                                    if (p[pixel[11]] > cb) {
                                                        if (p[pixel[12]] > cb) {
                                                            if (p[pixel[13]] > cb) {
                                                                if (p[pixel[14]] > cb) {
                                                                    if (p[pixel[3]] > cb) {
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
                                                            if (p[pixel[15]] < c_b) {
                                                            } else if (p[pixel[3]] < c_b) {
                                                                if (p[pixel[4]] < c_b) {
                                                                    if (p[pixel[5]] < c_b) {
                                                                    } else {
                                                                        continue;
                                                                    }
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
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
                        if (p[pixel[4]] > cb) {
                            if (p[pixel[6]] > cb) {
                                if (p[pixel[7]] > cb) {
                                    if (p[pixel[8]] > cb) {
                                        if (p[pixel[9]] > cb) {
                                            if (p[pixel[10]] > cb) {
                                                if (p[pixel[11]] > cb) {
                                                    if (p[pixel[12]] > cb) {
                                                        if (p[pixel[13]] > cb) {
                                                            if (p[pixel[3]] > cb) {
                                                                if (p[pixel[2]] > cb) {
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
                                                        if (p[pixel[14]] < c_b) {
                                                            if (p[pixel[15]] < c_b) {
                                                            } else if (p[pixel[3]] < c_b) {
                                                                if (p[pixel[4]] < c_b) {
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else if (p[pixel[2]] < c_b) {
                                                            if (p[pixel[3]] < c_b) {
                                                                if (p[pixel[4]] < c_b) {
                                                                } else {
                                                                    continue;
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        } else {
                                                            continue;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                continue;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else {
                                    continue;
                                }
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
                                    if (p[pixel[9]] > cb) {
                                        if (p[pixel[10]] > cb) {
                                            if (p[pixel[11]] > cb) {
                                                if (p[pixel[12]] > cb) {
                                                    if (p[pixel[3]] > cb) {
                                                        if (p[pixel[2]] > cb) {
                                                            if (p[pixel[1]] > cb) {
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
                                    if (p[pixel[9]] < c_b) {
                                        if (p[pixel[10]] < c_b) {
                                            if (p[pixel[11]] < c_b) {
                                                if (p[pixel[12]] < c_b) {
                                                    if (p[pixel[3]] < c_b) {
                                                        if (p[pixel[2]] < c_b) {
                                                            if (p[pixel[1]] < c_b) {
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
                    } else {
                        continue;
                    }
                } else {
                    continue;
                }
                ret_corners.add(new ImagePoint(x, y));
                num_corners++;
                ret_num_corners++;

            }
        }

        ret_num_corners = num_corners;
        return ret_corners;
    }

    private int[] fast12_score(int[] i, int stride, List<ImagePoint> corners, int num_corners, int b) {
        int[] scores = new int[num_corners];
        int n;

        int[] pixel = new int[16];

        for (n = 0; n < num_corners; n++) {
            FASTUtils.make_offsets(pixel, stride, (int) corners.get(n).x, (int) corners.get(n).y);
            scores[n] = fast12_corner_score(i, pixel, b, corners.get(n), stride);
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

    private int fast12_corner_score(int[] p, int[] pixel, int bstart, ImagePoint currentP, int stride) {

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
                                                    if (p[pixel[10]] > cb) {
                                                        if (p[pixel[11]] > cb) {
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
                                    } else if (p[pixel[10]] > cb) {
                                        if (p[pixel[11]] > cb) {
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
                                } else if (p[pixel[9]] > cb) {
                                    if (p[pixel[10]] > cb) {
                                        if (p[pixel[11]] > cb) {
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
                            } else if (p[pixel[4]] < c_b) {
                                if (p[pixel[8]] > cb) {
                                    if (p[pixel[9]] > cb) {
                                        if (p[pixel[10]] > cb) {
                                            if (p[pixel[11]] > cb) {
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
                                } else if (p[pixel[8]] < c_b) {
                                    if (p[pixel[5]] < c_b) {
                                        if (p[pixel[6]] < c_b) {
                                            if (p[pixel[7]] < c_b) {
                                                if (p[pixel[9]] < c_b) {
                                                    if (p[pixel[10]] < c_b) {
                                                        if (p[pixel[11]] < c_b) {
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
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
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
                            if (p[pixel[15]] > cb) {
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
                                } else if (p[pixel[7]] < c_b) {
                                    if (p[pixel[4]] < c_b) {
                                        if (p[pixel[5]] < c_b) {
                                            if (p[pixel[6]] < c_b) {
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
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
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
                            if (p[pixel[4]] < c_b) {
                                if (p[pixel[5]] < c_b) {
                                    if (p[pixel[6]] < c_b) {
                                        if (p[pixel[8]] < c_b) {
                                            if (p[pixel[9]] < c_b) {
                                                if (p[pixel[10]] < c_b) {
                                                    if (p[pixel[11]] < c_b) {
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
                            } else {
                                is_not_a_corner();
                            }
                        } else {
                            is_not_a_corner();
                        }
                    } else if (p[pixel[2]] < c_b) {
                        if (p[pixel[6]] > cb) {
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
                                                            } else if (p[pixel[3]] > cb) {
                                                                if (p[pixel[4]] > cb) {
                                                                    if (p[pixel[5]] > cb) {
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
                            } else {
                                is_not_a_corner();
                            }
                        } else if (p[pixel[6]] < c_b) {
                            if (p[pixel[4]] < c_b) {
                                if (p[pixel[5]] < c_b) {
                                    if (p[pixel[7]] < c_b) {
                                        if (p[pixel[8]] < c_b) {
                                            if (p[pixel[9]] < c_b) {
                                                if (p[pixel[10]] < c_b) {
                                                    if (p[pixel[11]] < c_b) {
                                                        if (p[pixel[12]] < c_b) {
                                                            if (p[pixel[13]] < c_b) {
                                                                if (p[pixel[3]] < c_b) {
                                                                    is_a_corner();
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
                                                        if (p[pixel[15]] > cb) {
                                                            is_a_corner();
                                                        } else if (p[pixel[3]] > cb) {
                                                            if (p[pixel[4]] > cb) {
                                                                if (p[pixel[5]] > cb) {
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
                        } else {
                            is_not_a_corner();
                        }
                    } else if (p[pixel[6]] < c_b) {
                        if (p[pixel[4]] < c_b) {
                            if (p[pixel[5]] < c_b) {
                                if (p[pixel[7]] < c_b) {
                                    if (p[pixel[8]] < c_b) {
                                        if (p[pixel[9]] < c_b) {
                                            if (p[pixel[10]] < c_b) {
                                                if (p[pixel[11]] < c_b) {
                                                    if (p[pixel[12]] < c_b) {
                                                        if (p[pixel[13]] < c_b) {
                                                            if (p[pixel[14]] < c_b) {
                                                                if (p[pixel[3]] < c_b) {
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
                        } else {
                            is_not_a_corner();
                        }
                    } else {
                        is_not_a_corner();
                    }
                } else if (p[pixel[1]] < c_b) {
                    if (p[pixel[5]] > cb) {
                        if (p[pixel[6]] > cb) {
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
                                                            } else if (p[pixel[3]] > cb) {
                                                                if (p[pixel[4]] > cb) {
                                                                    is_a_corner();
                                                                } else {
                                                                    is_not_a_corner();
                                                                }
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else if (p[pixel[2]] > cb) {
                                                            if (p[pixel[3]] > cb) {
                                                                if (p[pixel[4]] > cb) {
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
                        } else {
                            is_not_a_corner();
                        }
                    } else if (p[pixel[5]] < c_b) {
                        if (p[pixel[4]] < c_b) {
                            if (p[pixel[6]] < c_b) {
                                if (p[pixel[7]] < c_b) {
                                    if (p[pixel[8]] < c_b) {
                                        if (p[pixel[9]] < c_b) {
                                            if (p[pixel[10]] < c_b) {
                                                if (p[pixel[11]] < c_b) {
                                                    if (p[pixel[12]] < c_b) {
                                                        if (p[pixel[3]] < c_b) {
                                                            if (p[pixel[2]] < c_b) {
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
                                                    if (p[pixel[14]] > cb) {
                                                        if (p[pixel[15]] > cb) {
                                                            is_a_corner();
                                                        } else if (p[pixel[3]] > cb) {
                                                            if (p[pixel[4]] > cb) {
                                                                is_a_corner();
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else if (p[pixel[2]] > cb) {
                                                        if (p[pixel[3]] > cb) {
                                                            if (p[pixel[4]] > cb) {
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
                    } else {
                        is_not_a_corner();
                    }
                } else if (p[pixel[5]] < c_b) {
                    if (p[pixel[4]] < c_b) {
                        if (p[pixel[6]] < c_b) {
                            if (p[pixel[7]] < c_b) {
                                if (p[pixel[8]] < c_b) {
                                    if (p[pixel[9]] < c_b) {
                                        if (p[pixel[10]] < c_b) {
                                            if (p[pixel[11]] < c_b) {
                                                if (p[pixel[12]] < c_b) {
                                                    if (p[pixel[13]] < c_b) {
                                                        if (p[pixel[3]] < c_b) {
                                                            if (p[pixel[2]] < c_b) {
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
                    } else {
                        is_not_a_corner();
                    }
                } else {
                    is_not_a_corner();
                }
            } else if (p[pixel[0]] < c_b) {
                if (p[pixel[1]] > cb) {
                    if (p[pixel[5]] > cb) {
                        if (p[pixel[4]] > cb) {
                            if (p[pixel[6]] > cb) {
                                if (p[pixel[7]] > cb) {
                                    if (p[pixel[8]] > cb) {
                                        if (p[pixel[9]] > cb) {
                                            if (p[pixel[10]] > cb) {
                                                if (p[pixel[11]] > cb) {
                                                    if (p[pixel[12]] > cb) {
                                                        if (p[pixel[3]] > cb) {
                                                            if (p[pixel[2]] > cb) {
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
                                                        if (p[pixel[14]] < c_b) {
                                                            if (p[pixel[15]] < c_b) {
                                                                is_a_corner();
                                                            } else if (p[pixel[3]] < c_b) {
                                                                if (p[pixel[4]] < c_b) {
                                                                    is_a_corner();
                                                                } else {
                                                                    is_not_a_corner();
                                                                }
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else if (p[pixel[2]] < c_b) {
                                                            if (p[pixel[3]] < c_b) {
                                                                if (p[pixel[4]] < c_b) {
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
                        } else {
                            is_not_a_corner();
                        }
                    } else {
                        is_not_a_corner();
                    }
                } else if (p[pixel[1]] < c_b) {
                    if (p[pixel[2]] > cb) {
                        if (p[pixel[6]] > cb) {
                            if (p[pixel[4]] > cb) {
                                if (p[pixel[5]] > cb) {
                                    if (p[pixel[7]] > cb) {
                                        if (p[pixel[8]] > cb) {
                                            if (p[pixel[9]] > cb) {
                                                if (p[pixel[10]] > cb) {
                                                    if (p[pixel[11]] > cb) {
                                                        if (p[pixel[12]] > cb) {
                                                            if (p[pixel[13]] > cb) {
                                                                if (p[pixel[3]] > cb) {
                                                                    is_a_corner();
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
                                                            if (p[pixel[15]] < c_b) {
                                                                is_a_corner();
                                                            } else if (p[pixel[3]] < c_b) {
                                                                if (p[pixel[4]] < c_b) {
                                                                    if (p[pixel[5]] < c_b) {
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
                            } else {
                                is_not_a_corner();
                            }
                        } else {
                            is_not_a_corner();
                        }
                    } else if (p[pixel[2]] < c_b) {
                        if (p[pixel[3]] > cb) {
                            if (p[pixel[15]] < c_b) {
                                if (p[pixel[7]] > cb) {
                                    if (p[pixel[4]] > cb) {
                                        if (p[pixel[5]] > cb) {
                                            if (p[pixel[6]] > cb) {
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
                            } else if (p[pixel[4]] > cb) {
                                if (p[pixel[5]] > cb) {
                                    if (p[pixel[6]] > cb) {
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
                                } else {
                                    is_not_a_corner();
                                }
                            } else {
                                is_not_a_corner();
                            }
                        } else if (p[pixel[3]] < c_b) {
                            if (p[pixel[4]] > cb) {
                                if (p[pixel[8]] > cb) {
                                    if (p[pixel[5]] > cb) {
                                        if (p[pixel[6]] > cb) {
                                            if (p[pixel[7]] > cb) {
                                                if (p[pixel[9]] > cb) {
                                                    if (p[pixel[10]] > cb) {
                                                        if (p[pixel[11]] > cb) {
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
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
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
                                                if (p[pixel[9]] < c_b) {
                                                    if (p[pixel[10]] < c_b) {
                                                        if (p[pixel[11]] < c_b) {
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
                                    } else if (p[pixel[10]] < c_b) {
                                        if (p[pixel[11]] < c_b) {
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
                                } else if (p[pixel[9]] < c_b) {
                                    if (p[pixel[10]] < c_b) {
                                        if (p[pixel[11]] < c_b) {
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
                            } else if (p[pixel[8]] < c_b) {
                                if (p[pixel[9]] < c_b) {
                                    if (p[pixel[10]] < c_b) {
                                        if (p[pixel[11]] < c_b) {
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
                        } else if (p[pixel[7]] > cb) {
                            if (p[pixel[4]] > cb) {
                                if (p[pixel[5]] > cb) {
                                    if (p[pixel[6]] > cb) {
                                        if (p[pixel[8]] > cb) {
                                            if (p[pixel[9]] > cb) {
                                                if (p[pixel[10]] > cb) {
                                                    if (p[pixel[11]] > cb) {
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
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else {
                                                        is_not_a_corner();
                                                    }
                                                } else {
                                                    is_not_a_corner();
                                                }
                                            } else {
                                                is_not_a_corner();
                                            }
                                        } else {
                                            is_not_a_corner();
                                        }
                                    } else {
                                        is_not_a_corner();
                                    }
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
                        if (p[pixel[4]] > cb) {
                            if (p[pixel[5]] > cb) {
                                if (p[pixel[7]] > cb) {
                                    if (p[pixel[8]] > cb) {
                                        if (p[pixel[9]] > cb) {
                                            if (p[pixel[10]] > cb) {
                                                if (p[pixel[11]] > cb) {
                                                    if (p[pixel[12]] > cb) {
                                                        if (p[pixel[13]] > cb) {
                                                            if (p[pixel[14]] > cb) {
                                                                if (p[pixel[3]] > cb) {
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
                                                        if (p[pixel[15]] < c_b) {
                                                            is_a_corner();
                                                        } else if (p[pixel[3]] < c_b) {
                                                            if (p[pixel[4]] < c_b) {
                                                                if (p[pixel[5]] < c_b) {
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
                        } else {
                            is_not_a_corner();
                        }
                    } else {
                        is_not_a_corner();
                    }
                } else if (p[pixel[5]] > cb) {
                    if (p[pixel[4]] > cb) {
                        if (p[pixel[6]] > cb) {
                            if (p[pixel[7]] > cb) {
                                if (p[pixel[8]] > cb) {
                                    if (p[pixel[9]] > cb) {
                                        if (p[pixel[10]] > cb) {
                                            if (p[pixel[11]] > cb) {
                                                if (p[pixel[12]] > cb) {
                                                    if (p[pixel[13]] > cb) {
                                                        if (p[pixel[3]] > cb) {
                                                            if (p[pixel[2]] > cb) {
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
                                                    if (p[pixel[14]] < c_b) {
                                                        if (p[pixel[15]] < c_b) {
                                                            is_a_corner();
                                                        } else if (p[pixel[3]] < c_b) {
                                                            if (p[pixel[4]] < c_b) {
                                                                is_a_corner();
                                                            } else {
                                                                is_not_a_corner();
                                                            }
                                                        } else {
                                                            is_not_a_corner();
                                                        }
                                                    } else if (p[pixel[2]] < c_b) {
                                                        if (p[pixel[3]] < c_b) {
                                                            if (p[pixel[4]] < c_b) {
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
                                if (p[pixel[9]] > cb) {
                                    if (p[pixel[10]] > cb) {
                                        if (p[pixel[11]] > cb) {
                                            if (p[pixel[12]] > cb) {
                                                if (p[pixel[3]] > cb) {
                                                    if (p[pixel[2]] > cb) {
                                                        if (p[pixel[1]] > cb) {
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
                                if (p[pixel[9]] < c_b) {
                                    if (p[pixel[10]] < c_b) {
                                        if (p[pixel[11]] < c_b) {
                                            if (p[pixel[12]] < c_b) {
                                                if (p[pixel[3]] < c_b) {
                                                    if (p[pixel[2]] < c_b) {
                                                        if (p[pixel[1]] < c_b) {
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
