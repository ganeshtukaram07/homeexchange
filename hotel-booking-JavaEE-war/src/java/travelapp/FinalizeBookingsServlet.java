/**
 *     Copyright (c) KU Leuven Research and Development - iMinds-DistriNet
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 *     Administrative Contact: dnet-project-office@cs.kuleuven.be
 *     Technical Contact: stefan.walraven@cs.kuleuven.be
 */
package travelapp;

import java.io.IOException;
import java.util.List;
import javax.ejb.EJB;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import travelapp.hotel.BookingException;

import travelapp.hotel.entity.Booking;
import travelapp.session.BookingSession;

@SuppressWarnings("serial")
public class FinalizeBookingsServlet extends HttpServlet {

    @EJB
    private BookingSession bookingSession;
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        
        HttpSession session = req.getSession();
                    
        PlannedTrip trip = (PlannedTrip)session.getAttribute("trip");
        if(trip == null || trip.getBookings().isEmpty()) {
            req.setAttribute("msg", "There are no tentative bookings to present.");
            try {
                req.getRequestDispatcher("output.jsp").forward(req, resp);
                return;
            } catch (ServletException e) {
                // redirect to home page
                resp.sendRedirect("index.jsp");
                return;
            }
        }
            
        List<Booking> bookings = null;
        try{
            bookings = bookingSession.finalizeBookings(trip.getBookings());
        } catch(BookingException ex) {
            req.setAttribute("msg",ex.getMessage());
            try {
                req.getRequestDispatcher("output.jsp").forward(req, resp);
                return;
            } catch (ServletException e) {
                // redirect to home page
                resp.sendRedirect("index.jsp");
                return;
            }
        }
            
        try {
            req.setAttribute("booked", bookings);
            // send email to guest with bill
            // give overview of finalized bookings
            req.getRequestDispatcher("finalized.jsp").forward(req, resp);
        } catch (ServletException e) {
            // redirect to home page
            resp.sendRedirect("index.jsp");
        }
    }
}
