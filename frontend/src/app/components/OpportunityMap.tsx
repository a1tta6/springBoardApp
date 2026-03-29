import React from 'react';
import { MapContainer, TileLayer, Marker, Popup, useMapEvents } from 'react-leaflet';
import { useNavigate } from 'react-router';
import L from 'leaflet';
import MarkerClusterGroup from 'react-leaflet-cluster';
import { Company, Opportunity } from '../types';
import { Badge } from './ui/badge';
import { MapPin, Building2, Calendar, Banknote } from 'lucide-react';
import 'leaflet/dist/leaflet.css';

delete (L.Icon.Default.prototype as { _getIconUrl?: unknown })._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-icon-2x.png',
  iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-icon.png',
  shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-shadow.png',
});

interface OpportunityMapProps {
  opportunities: Opportunity[];
  companies: Company[];
  favorites: string[];
  onOpportunityClick?: (opportunity: Opportunity) => void;
  onBoundsChange?: (bounds: { minLat: number; maxLat: number; minLng: number; maxLng: number }) => void;
}

const opportunityTypeColors: Record<string, string> = {
  internship: '#3b82f6',
  vacancy: '#10b981',
  mentorship: '#8b5cf6',
  event: '#f59e0b',
};

const opportunityTypeLabels: Record<string, string> = {
  internship: 'Стажировка',
  vacancy: 'Вакансия',
  mentorship: 'Менторство',
  event: 'Мероприятие',
};

const MapEvents: React.FC<{
  onBoundsChange?: (bounds: { minLat: number; maxLat: number; minLng: number; maxLng: number }) => void;
}> = ({ onBoundsChange }) => {
  const map = useMapEvents({
    moveend: () => {
      if (onBoundsChange) {
        const bounds = map.getBounds();
        onBoundsChange({
          minLat: bounds.getSouth(),
          maxLat: bounds.getNorth(),
          minLng: bounds.getWest(),
          maxLng: bounds.getEast(),
        });
      }
    },
  });

  // Initial bounds report
  React.useEffect(() => {
    if (onBoundsChange) {
      const bounds = map.getBounds();
      onBoundsChange({
        minLat: bounds.getSouth(),
        maxLat: bounds.getNorth(),
        minLng: bounds.getWest(),
        maxLng: bounds.getEast(),
      });
    }
  }, [map, onBoundsChange]);

  return null;
};

const createCustomIcon = (type: string, isFavorite: boolean) => {
  const color = opportunityTypeColors[type] || '#6b7280';
  const svgIcon = `
    <svg width="25" height="41" viewBox="0 0 25 41" xmlns="http://www.w3.org/2000/svg">
      <path d="M12.5 0C5.6 0 0 5.6 0 12.5C0 21.9 12.5 41 12.5 41S25 21.9 25 12.5C25 5.6 19.4 0 12.5 0Z"
            fill="${color}"
            stroke="${isFavorite ? '#fbbf24' : '#000'}"
            stroke-width="${isFavorite ? '2' : '1'}"/>
      <circle cx="12.5" cy="12.5" r="6" fill="white"/>
    </svg>
  `;
  return L.divIcon({
    html: svgIcon,
    className: 'custom-marker',
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
  });
};

export const OpportunityMap: React.FC<OpportunityMapProps> = ({
  opportunities,
  companies,
  favorites,
  onOpportunityClick,
  onBoundsChange,
}) => {
  const defaultCenter: [number, number] = [55.751244, 37.618423];

  return (
    <div className="h-full w-full relative">
      <style>
        {`
          .custom-cluster-icon {
            background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
            border: 2px solid white;
            border-radius: 50%;
            color: white;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: bold;
            font-size: 14px;
            box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);
          }
          .custom-cluster-icon:hover {
            transform: scale(1.1);
            transition: transform 0.2s ease;
          }
        `}
      </style>
      <MapContainer center={defaultCenter} zoom={5} className="h-full w-full rounded-lg" style={{ zIndex: 0 }}>
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />
        <MapEvents onBoundsChange={onBoundsChange} />
        <MarkerClusterGroup
          chunkedLoading
          iconCreateFunction={(cluster: any) => {
            const count = cluster.getChildCount();
            return L.divIcon({
              html: `<span>${count}</span>`,
              className: 'custom-cluster-icon',
              iconSize: L.point(40, 40),
            });
          }}
        >
          {opportunities.map((opportunity) => {
            const company = companies.find((item) => item.id === opportunity.companyId);
            const isFavorite = favorites.includes(opportunity.id);

            return (
              <Marker
                key={opportunity.id}
                position={opportunity.location.coordinates}
                icon={createCustomIcon(opportunity.type, isFavorite)}
                eventHandlers={{
                  click: () => {
                    if (onOpportunityClick) {
                      onOpportunityClick(opportunity);
                    }
                  },
                }}
              >
                <Popup>
                  <div className="p-2 min-w-[250px]">
                    <h3 className="font-semibold mb-2">{opportunity.title}</h3>

                    <div className="flex items-center gap-2 mb-2">
                      <Building2 className="w-4 h-4 text-gray-500" />
                      <span className="text-sm">{company?.name}</span>
                    </div>

                    <div className="flex items-center gap-2 mb-2">
                      <MapPin className="w-4 h-4 text-gray-500" />
                      <span className="text-sm">{opportunity.location.address || opportunity.location.city}</span>
                    </div>

                    {opportunity.salary && (
                      <div className="flex items-center gap-2 mb-2">
                        <Banknote className="w-4 h-4 text-gray-500" />
                        <span className="text-sm">
                          {opportunity.salary.min?.toLocaleString()} - {opportunity.salary.max?.toLocaleString()} {opportunity.salary.currency}
                        </span>
                      </div>
                    )}

                    {opportunity.eventDate && (
                      <div className="flex items-center gap-2 mb-2">
                        <Calendar className="w-4 h-4 text-gray-500" />
                        <span className="text-sm">{new Date(opportunity.eventDate).toLocaleDateString('ru-RU')}</span>
                      </div>
                    )}

                    <div className="flex gap-2 mt-3">
                      <Badge variant="secondary" className="text-xs">
                        {opportunityTypeLabels[opportunity.type]}
                      </Badge>
                      <Badge variant="outline" className="text-xs">
                        {opportunity.workFormat === 'office' ? 'Офис' : opportunity.workFormat === 'hybrid' ? 'Гибрид' : 'Удаленно'}
                      </Badge>
                    </div>
                    <a
                      href={`/opportunity/${opportunity.id}`}
                      className="block mt-3 text-sm text-blue-600 hover:text-blue-800 font-medium"
                      onClick={(e) => {
                        e.preventDefault();
                        window.location.href = `/opportunity/${opportunity.id}`;
                      }}
                    >
                      Подробнее →
                    </a>
                  </div>
                </Popup>
              </Marker>
            );
          })}
        </MarkerClusterGroup>
      </MapContainer>
    </div>
  );
};
